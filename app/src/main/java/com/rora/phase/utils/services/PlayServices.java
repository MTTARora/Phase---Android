package com.rora.phase.utils.services;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.RoraLog;
import com.rora.phase.R;
import com.rora.phase.binding.PlatformBinding;
import com.rora.phase.computers.IdentityManager;
import com.rora.phase.discovery.DiscoveryService;
import com.rora.phase.model.Game;
import com.rora.phase.model.Host;
import com.rora.phase.model.UserPlayingData;
import com.rora.phase.nvstream.NvConnection;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.nvstream.http.NvApp;
import com.rora.phase.nvstream.http.NvHTTP;
import com.rora.phase.nvstream.http.PairingManager;
import com.rora.phase.nvstream.mdns.MdnsComputer;
import com.rora.phase.nvstream.mdns.MdnsDiscoveryListener;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.utils.NetHelper;
import com.rora.phase.utils.ServerHelper;
import com.rora.phase.utils.callback.PlayGameProgressCallBack;
import com.rora.phase.utils.network.realtime.playhub.PlayHub;
import com.rora.phase.utils.network.realtime.playhub.PlayHubListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PlayServices extends Service {
    private final PlayServices.ComputerManagerBinder binder = new PlayServices.ComputerManagerBinder();
    private final ComputerServices computerServices = new ComputerServices();

    private MutableLiveData<UserPlayingData.PlayingState> state = new MutableLiveData<>();
    private PollingTuple pollingTuple;
    private PlayGameProgressCallBack listener = null;
    private IdentityManager idManager;
    UserRepository userRepository;
    private PlayHub playHub;
    private Thread connectThread;
    private Game currentGame;

    private DiscoveryService.DiscoveryBinder discoveryBinder;
    private final ServiceConnection discoveryServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            synchronized (discoveryServiceConnection) {
                DiscoveryService.DiscoveryBinder privateBinder = ((DiscoveryService.DiscoveryBinder) binder);

                // Set us as the event listener
                privateBinder.setListener(createDiscoveryListener());

                // Signal a possible waiter that we're all setup
                discoveryBinder = privateBinder;
                discoveryServiceConnection.notifyAll();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            discoveryBinder = null;
        }
    };


    //------------------------------------ Lifecycle ----------------------------------

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        if (discoveryBinder == null)
            bindService(new Intent(this, DiscoveryService.class), discoveryServiceConnection, Service.BIND_AUTO_CREATE);

        // Lookup or generate this device's UID
        idManager = new IdentityManager(this);
        userRepository = new UserRepository(getApplicationContext());
        setCurrentState(UserPlayingData.PlayingState.IDLE);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (state.getValue() == UserPlayingData.PlayingState.IN_PROGRESS || state.getValue() == UserPlayingData.PlayingState.PLAYING) {
            stopConnect(null, null);
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (discoveryBinder != null) {
            unbindService(discoveryServiceConnection);
        }
        // FIXME: Should await termination here but we have timeout issues in HttpURLConnection
    }


    //--------------------------------------------------------------------------------------

    private MdnsDiscoveryListener createDiscoveryListener() {
        return new MdnsDiscoveryListener() {
            @Override
            public void notifyComputerAdded(MdnsComputer computer) {
                ComputerDetails details = new ComputerDetails();

                // Populate the computer template with mDNS info
                if (computer.getLocalAddress() != null) {
                    details.localAddress = computer.getLocalAddress().getHostAddress();

                    // Since we're on the same network, we can use STUN to find
                    // our WAN address, which is also very likely the WAN address
                    // of the PC. We can use this later to connect remotely.
                    if (computer.getLocalAddress() instanceof Inet4Address) {
                        computerServices.populateExternalAddress(details);
                    }
                }
                if (computer.getIpv6Address() != null) {
                    details.ipv6Address = computer.getIpv6Address().getHostAddress();
                }

                // Kick off a server info poll on this machine
                if (!computerServices.addComputerBlocking(details)) {
                    RoraLog.warning("Auto-discovered PC failed to respond: " + details);
                }
            }

            @Override
            public void notifyComputerRemoved(MdnsComputer computer) {
                // Nothing to do here
            }

            @Override
            public void notifyDiscoveryFailure(Exception e) {
                RoraLog.severe("mDNS discovery failed");
                e.printStackTrace();
            }
        };
    }


    //====================== PLAYING GAME STEPS =========================

    /**
     * Start play progress
     */
    public void startConnectProgress(Activity activity, Game game, PlayGameProgressCallBack callBack) {
        if (state.getValue() != UserPlayingData.PlayingState.IDLE)
            return;

        connectThread = new Thread(() -> {
            RoraLog.info("Play game - STEP 1: Start Connecting");
            setCurrentState(UserPlayingData.PlayingState.IN_PROGRESS);
            this.currentGame = game;
            userRepository.storeCurrentGame(game);

            listener.onStart(false);
            callBack.onStart(false);

            //STEP 1: Connect to hub
            playHub = new PlayHub();
            playHub.startConnect(getApplicationContext(), new PlayHubListener() {
                @Override
                public void onConnected() {
                    listener.onStart(true);
                    callBack.onStart(true);
                    RoraLog.info("Play game - STEP 2: Waiting for server establishing connection");
                }

                //STEP 2: Waiting for server establishing connection
                @Override
                public void onConnectionEstablished(String msg, boolean isSuccess) {
                    if (!isSuccess) {
                        RoraLog.info("Play game - STEP 2: Establishing connection with server failed - " + msg);
                        listener.onConnectionEstablished(false);
                        callBack.onConnectionEstablished(false);
                        return;
                    }

                    listener.onFindAHost(false);
                    callBack.onFindAHost(false);
                    //STEP 3: Get host data
                    RoraLog.info("Play game - STEP 3: Get available host");
                    String deviceName = Build.MANUFACTURER
                            + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                            + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();

                    userRepository.getAvailableHost(deviceName, deviceName, game.getId().toString(),(errMsg, response) -> {
                        listener.onFindAHost(true);
                        callBack.onFindAHost(true);
                        if (errMsg != null) {
                            RoraLog.warning("Play Game - Error: " + errMsg);
                            stopConnect(callBack, errMsg);
                            return;
                        }

                        new Thread(() -> {
                            try {
                                //STEP 3.1: Check queue
                                if (response.queue != null) {
                                    RoraLog.info("Play game: So many players are playing right now, please wait!");
                                    setCurrentState(UserPlayingData.PlayingState.IN_QUEUE);
                                    listener.onQueueUpdated(true, response.queue.getTotal(), response.queue.getCurrentPosition());
                                    callBack.onQueueUpdated(true, response.queue.getTotal(), response.queue.getCurrentPosition());
                                    return;
                                }

                                //STEP 4: Pair
                                continueProgressWithAvailableHost(response.host, callBack);
                            } catch (Exception e) {
                                RoraLog.info("Play Game - Error: " + e.getMessage());
                                stopConnect(callBack, e.getMessage());
                            }
                        }).start();
                    });
                }

                @Override
                public void onUpdatePlayQueue(int position) {
                    if (getCurrentState() != UserPlayingData.PlayingState.IN_QUEUE) {
                        RoraLog.info("Play game: So many players are playing right now, please wait!");
                        setCurrentState(UserPlayingData.PlayingState.IN_QUEUE);
                        listener.onQueueUpdated(true, 0, position);
                        callBack.onQueueUpdated(true, 0, position);
                        return;
                    }

                    listener.onQueueUpdated(false, 0, position);
                }

                @Override
                public void onHostAvailable(Host host) {
                    continueProgressWithAvailableHost(host, callBack);
                }

                @Override
                public void onAppReady(boolean isSuccess) {
                    if (!isSuccess) {
                        stopConnect(callBack, getResources().getString(R.string.undetected_error_from_server));
                        return;
                    }

                    //STEP 7: Play
                    RoraLog.info("Play game - STEP 7: Start remote connect...");
                    listener.onStartConnect(false);
                    callBack.onStartConnect(false);
                    String error = start(activity, binder);
                    if (error != null) {
                        RoraLog.info("Play Game - Error: " + error);
                        stopConnect(callBack, error);
                        return;
                    }

                    setCurrentState(UserPlayingData.PlayingState.PLAYING);
                    RoraLog.info("Play game - FINAL: Everything is done, ready to play!");
                    listener.onStartConnect(true);
                    callBack.onStartConnect(true);
                }

                @Override
                public void onDisconnected(int code) {
                    String err = null;
                    switch (code) {
                        case 401:
                            userRepository.signOut();
                            callBack.onError("login session ended");
                            listener.onError("Your login session has ended, please login again!");
                            break;
                        case 500:
                            err = getResources().getString(R.string.undetected_error_from_server);
                            break;
                        default:
                            break;
                    }

                    RoraLog.info("Play Game - Hub disconnected!");
                    stopConnect(callBack, err);
                }
            });
        });
        connectThread.setName("Play Service - playing progress");
        connectThread.start();
    }

    private void continueProgressWithAvailableHost(Host host, PlayGameProgressCallBack callBack) {
        try {
            //STEP 3: Pair
            RoraLog.info("Play game - STEP 3: Start pairing...");
            listener.onPairPc(false);
            callBack.onPairPc(false);
            ComputerDetails computer = new ComputerDetails(host);
            pollingTuple = new PollingTuple(computer, null);
            NvHTTP.HTTPS_PORT1 = computer.httpsPort1;
            NvHTTP.HTTP_PORT2 = computer.httpsPort1 + 1;

            String err = startPairing(callBack);
            if (err != null) {
                RoraLog.info("Play Game - Error: " + err);
                stopConnect(callBack, err);
                return;
            }
            listener.onPairPc(true);
            callBack.onPairPc(true);
            Thread.sleep(1000);

            //STEP 5: Get game list from nvdia
            RoraLog.info("Play game - STEP 5: Getting app list from nvidia...");
            listener.onGetHostApps(false);
            callBack.onGetHostApps(false);
            err = getNecessaryAppFromNvidia();
            if (err != null) {
                RoraLog.info("Play Game - Error: " + err);
                stopConnect(callBack, err);
                return;
            }
            listener.onGetHostApps(true);
            callBack.onGetHostApps(true);

            //STEP 6: Request host to prepare app if pair success
            RoraLog.info("Play game - STEP 6: Pair success, requesting host for preparation app...");
            listener.onPrepareHost(false);
            callBack.onPrepareHost(false);
            userRepository.prepareAppHost(currentGame.getId().toString(), "1", null, null, (error, data) -> {
                if (error != null) {
                    RoraLog.info("Play Game - Error: " + error);
                    stopConnect(callBack, error);
                    return;
                }
            });
            listener.onPrepareHost(true);
            callBack.onPrepareHost(true);
        } catch (Exception e) {
            RoraLog.info("Play Game - Error: " + e.getMessage());
            stopConnect(callBack, e.getMessage());
        }
    }

    /**
     * STEP 3: Pair computer
     * <p>
     * - Send paring pin to server and try to pair with computer
     * Use this method in a thread
     *
     * @return An error msg if failed
     */
    private String startPairing(PlayGameProgressCallBack callBack) {
        String err = computerServices.checkPairCondition(pollingTuple.computer, binder);

        try {
            if (err != null)
                return err;

            NvHTTP httpConn = new NvHTTP(ServerHelper.getCurrentAddressFromComputer(pollingTuple.computer),
                    binder.getUniqueId(),
                    pollingTuple.computer.serverCert,
                    PlatformBinding.getCryptoProvider(getApplication().getApplicationContext()));

            if (httpConn.getPairState() == PairingManager.PairState.PAIRED)
                return null;

            //STEP 4.1: GENERATE AND WAIT FOR SERVER HANDEL PIN
            final String pinStr = PairingManager.generatePinString();

            //STEP 4.2: SEND PIN TO HOST
            RoraLog.info("Pairing - Waiting for pin confirmation: " + pinStr);
            userRepository.sendPinToHost(pinStr, (errMsg, data) -> {
                if (errMsg != null) {
                    RoraLog.info("Play Game - Error: " + errMsg);
                    stopConnect(callBack, errMsg);
                }
            });

            PairingManager pm = httpConn.getPairingManager();
            PairingManager.PairState pairState = pm.pair(httpConn.getServerInfo(), pinStr);

            switch (pairState) {
                case PIN_WRONG:
                    err = getApplication().getResources().getString(R.string.pair_incorrect_pin);
                    break;
                case FAILED:
                    err = getApplication().getResources().getString(R.string.pair_fail);
                    break;
                case ALREADY_IN_PROGRESS:
                    err = getApplication().getResources().getString(R.string.pair_already_in_progress);
                    break;
                case PAIRED:
                    err = null;
                    // Pin this certificate for later HTTPS use
                    pollingTuple.computer.serverCert = pm.getPairedCert();

                    // Invalidate reachability information after pairing to force a refresh before reading pair state again
                    computerServices.invalidateStateForComputer();
                    break;
                case NOT_PAIRED:
                default:
                    err = getApplication().getResources().getString(R.string.undetected_error);
                    break;
            }
        } catch (UnknownHostException e) {
            err = getApplication().getResources().getString(R.string.error_unknown_host);
        } catch (FileNotFoundException e) {
            err = getApplication().getResources().getString(R.string.error_404);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            err = e.getMessage();
        }

        RoraLog.info("Pairing - " + (err == null ? "Success" : "Err: " + err));
        return err;
    }

    /**
     * STEP 4: Connect to pc
     * <p>
     * - Get app list from nvidia
     *
     * @return An error msg if failed
     */
    private String getNecessaryAppFromNvidia() {
        try {
            NvHTTP http = new NvHTTP(pollingTuple.computer.activeAddress, idManager.getUniqueId(), pollingTuple.computer.serverCert, PlatformBinding.getCryptoProvider(this));
            String appList = http.getAppListRaw();
            if (appList != null) {
                pollingTuple.computer.rawAppList = appList;
                pollingTuple.computer.appList = NvHTTP.getAppListByReader(new StringReader(appList));
                for (NvApp nvApp : pollingTuple.computer.appList) {
                    if (nvApp.getAppName().equals("mstsc.exe")) {
                        pollingTuple.computer.setRemoteApp(nvApp);
                        break;
                    }
                }

                return pollingTuple.computer.getRemoteApp() == null ? "Couldn't find necessary app from host!" : null;
            }
            return "Couldn't get app list from nvidia";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    /**
     * STEP 5: Start remote connect
     */
    private String start(Activity activity, PlayServices.ComputerManagerBinder managerBinder) {
        if (pollingTuple.computer.getRemoteApp() != null) {
            ServerHelper.doStart(activity, pollingTuple.computer.getRemoteApp(), pollingTuple.computer, managerBinder);
            return null;
        } else
            return "Couldn't find necessary app from host!";
    }

    /**
     * STEP 6 - FINAL: Counting playtime
     */
    public void startCountingPlaytime() {

    }

    public void pauseSession() {
        if (pollingTuple.computer.getRemoteApp() != null) {
            setCurrentState(UserPlayingData.PlayingState.PAUSED);
            listener.onPaused(null);
        } else {
            listener.onResumed("Couldn't find necessary app from host!");
        }
    }

    public void resumeSession(Activity activity, PlayServices.ComputerManagerBinder managerBinder) {
        if (pollingTuple.computer.getRemoteApp() != null) {
            ServerHelper.doStart(activity, pollingTuple.computer.getRemoteApp(), pollingTuple.computer, managerBinder);
            listener.onResumed(null);
        } else {
            listener.onResumed("Couldn't find necessary app from host!");
        }
    }

    /**
     * Use this method in a thread
     *
     * @return An error msg if failed
     */
    public void stopConnect(PlayGameProgressCallBack callBack, String err) {
        if (state.getValue() == UserPlayingData.PlayingState.IDLE)
            return;

        try {
            setCurrentState(UserPlayingData.PlayingState.IN_STOP_PROGRESS);
            RoraLog.info("Play game: Stop connecting");
            listener.onStopConnect(false, err);
            if (callBack != null)
                callBack.onStopConnect(false, err);
            stopNvdiaConnect();
            playHub.stopConnect();
            Thread.sleep(1000);

            if (connectThread != null)
                connectThread.interrupt();
            currentGame = null;
            pollingTuple = null;
            userRepository.storeCurrentGame(null);
            //listener = null;
            unbindService(discoveryServiceConnection);
            bindService(new Intent(this, DiscoveryService.class), discoveryServiceConnection, Service.BIND_AUTO_CREATE);
            connectThread = null;

            RoraLog.info("Play game - Stop connect success");
            setCurrentState(UserPlayingData.PlayingState.STOPPED);
            listener.onStopConnect(true, err);
            if (callBack != null)
                callBack.onStopConnect(true, err);

            setCurrentState(UserPlayingData.PlayingState.IDLE);
        } catch (Exception ex) {
            listener.onStopConnect(true, ex.getMessage());
            if (callBack != null)
                callBack.onStopConnect(true, ex.getMessage());
        }
    }

    private void stopNvdiaConnect() {
        new Thread(() -> {
            try {
                NvHTTP http = new NvHTTP(pollingTuple.computer.activeAddress, idManager.getUniqueId(), pollingTuple.computer.serverCert, PlatformBinding.getCryptoProvider(this));
                http.unpair();
                RoraLog.warning("Play game: stop nvidia connect success!");
            } catch (Exception e) {
                e.printStackTrace();
                RoraLog.warning("Play game: stop nvidia connect err: " + e.getMessage());
            }
        }).start();
    }

    //===================================================================

    private boolean isStopPlaying() {
        return state.getValue() == UserPlayingData.PlayingState.STOPPED;
    }

    private UserPlayingData.PlayingState getCurrentState() {
        return isPaused ? UserPlayingData.PlayingState.PAUSED : state.getValue();
    }

    private boolean isPaused = false;

    private void setCurrentState(UserPlayingData.PlayingState state) {
        isPaused = state == UserPlayingData.PlayingState.PAUSED;
        this.state.postValue(state);
    }


    //--------------------------------- Support Classes -------------------------------

    public class ComputerManagerBinder extends Binder {
        private static final int POLL_DATA_TTL_MS = 30000;
        private static final int MDNS_QUERY_PERIOD_MS = 1000;

        public void startConnectProgress(Activity activity, Game selectedGame, PlayGameProgressCallBack playProgressCallBack) {
            PlayServices.this.startConnectProgress(activity, selectedGame, playProgressCallBack);
        }

        public void pauseSession() {
            PlayServices.this.pauseSession();
        }

        public void resumeSession(Activity activity) {
            PlayServices.this.resumeSession(activity, this);
        }

        public void stopOrResumeConnect(Activity activity, PlayGameProgressCallBack playProgressCallBack) {
            if (getCurrentState() == UserPlayingData.PlayingState.PAUSED)
                resumeSession(activity);
            else
                PlayServices.this.stopConnect(playProgressCallBack, null);
        }

        public void stopConnect(PlayGameProgressCallBack playProgressCallBack) {
            PlayServices.this.stopConnect(playProgressCallBack, null);
        }

        public void startPolling(PlayGameProgressCallBack listener) {
            if (pollingTuple == null)
                return;

            RoraLog.info("Start updating computer");
            //PlayServices.this.listener = listener;

            // Start mDNS auto discovery too
            discoveryBinder.startDiscovery(MDNS_QUERY_PERIOD_MS);

            // Enforce the poll data TTL
            if (SystemClock.elapsedRealtime() - pollingTuple.lastSuccessfulPollMs > POLL_DATA_TTL_MS) {
                RoraLog.info("Timing out polled state for " + pollingTuple.computer.name);
                pollingTuple.computer.state = ComputerDetails.State.UNKNOWN;
            }

            // Report this computer initially
            //listener.onComputerUpdated(pollingTuple.computer);
        }

        public void stopPolling() {
            RoraLog.info("Stop polling/updating computer");

            // Just call the unbind handler to cleanup
            PlayServices.this.onUnbind(null);
        }

        public void waitForReady() {
            synchronized (discoveryServiceConnection) {
                try {
                    while (discoveryBinder == null) {
                        // Wait for the bind notification
                        discoveryServiceConnection.wait(1000);
                    }
                } catch (InterruptedException ignored) {
                }
            }
        }

        public String getUniqueId() {
            return idManager.getUniqueId();
        }

        public boolean isStopPlaying() {
            return PlayServices.this.isStopPlaying();
        }

        public UserPlayingData.PlayingState getCurrentState() {
            return PlayServices.this.getCurrentState();
        }

        public void setCurrentState(UserPlayingData.PlayingState state) {
            PlayServices.this.setCurrentState(state);
        }

        public void setListener(PlayGameProgressCallBack callBack) {
            PlayServices.this.listener = callBack;
        }

        public LiveData<UserPlayingData.PlayingState> setStateListener() {
            return PlayServices.this.state;
        }

        public Game getCurrentGame() {
            return PlayServices.this.currentGame;
        }

    }

    private class ComputerServices {
        private final Lock defaultNetworkLock = new ReentrantLock();

        private static final int FAST_POLL_TIMEOUT = 1000;
        private static final int OFFLINE_POLL_TRIES = 5;
        private static final int INITIAL_POLL_TRIES = 2;
        private static final int SERVERINFO_POLLING_PERIOD_MS = 1500;

        /**
         * Fill the necessary computer details
         *
         * @return true if success
         */
        public boolean addComputerBlocking(ComputerDetails fakeDetails) {
            // Block while we try to fill the details
            try {
                // We cannot use runPoll() here because it will attempt to persist the state of the machine
                // in the database, which would be bad because we don't have our pinned cert loaded yet.
                pollingTuple = new PollingTuple(fakeDetails, null);
                NvHTTP.HTTPS_PORT1 = fakeDetails.httpsPort1;
                NvHTTP.HTTP_PORT2 = fakeDetails.httpsPort1 + 1;
                if (pollComputer(pollingTuple.computer)) {
                    // Poll again, possibly with the pinned cert, to get accurate pairing information.
                    runPoll(pollingTuple.computer, true, 0);

                    // If the machine is reachable, it was successful
                    if (pollingTuple.computer.state == ComputerDetails.State.ONLINE) {
                        RoraLog.info("Adding PC - New PC (" + pollingTuple.computer.name + ") is UUID " + pollingTuple.computer.uuid);

                        // Start a polling thread for this machine
                        startTupleThread();
                        return true;
                    } else return false;
                }
            } catch (InterruptedException e) {
                return false;
            }

            return false;
        }

        /**
         * Do a TCP-level connection to the HTTP server to see if it's listening.
         *
         * @return true if success
         */
        private boolean pollComputer(ComputerDetails details) throws InterruptedException {
            ComputerDetails polledDetails;

            // Do a TCP-level connection to the HTTP server to see if it's listening.
            // Do not write this address to details.activeAddress because:
            // a) it's only a candidate and may be wrong (multiple PCs behind a single router)
            // b) if it's null, it will be unexpectedly nulling the activeAddress of a possibly online PC
            //LimeLog.info("Adding PC - Starting fast poll for "+details.name+" ("+details.localAddress +", "+details.remoteAddress +", "+details.manualAddress+", "+details.ipv6Address+")");
            String candidateAddress = fastPollPc(details.localAddress, details.remoteAddress, details.manualAddress, details.ipv6Address);
            //LimeLog.info("Adding PC - Fast poll for "+details.name+" returned candidate address: "+candidateAddress);

            // If no connection could be established to either IP address, there's nothing we can do
            if (candidateAddress == null) {
                RoraLog.info("Adding PC - Couldn't connect to this IP address " + details.manualAddress);
                return false;
            }

            // Try using the active address from fast-poll
            polledDetails = tryPollIp(details, candidateAddress);
            if (polledDetails == null) {
                // If that failed, try all unique addresses except what we've already tried
                HashSet<String> uniqueAddresses = new HashSet<>();
                uniqueAddresses.add(details.localAddress);
                uniqueAddresses.add(details.manualAddress);
                uniqueAddresses.add(details.remoteAddress);
                uniqueAddresses.add(details.ipv6Address);
                for (String addr : uniqueAddresses) {
                    if (addr == null || addr.equals(candidateAddress)) {
                        continue;
                    }
                    polledDetails = tryPollIp(details, addr);
                    if (polledDetails != null) {
                        break;
                    }
                }
            }

            if (polledDetails != null) {
                details.update(polledDetails);
                return true;
            } else {
                return false;
            }
        }

        /**
         * Make a listener if computer was modified
         *
         * @return true if it was modified
         */
        private boolean runPoll(ComputerDetails details, boolean newPc, int offlineCount) throws InterruptedException {
            if (isStopPlaying()) {
                stopConnect(null, null);
            }
            final int pollTriesBeforeOffline = details.state == ComputerDetails.State.UNKNOWN ? INITIAL_POLL_TRIES : OFFLINE_POLL_TRIES;

            // Poll the machine
            try {
                if (!pollComputer(details)) {
                    if (!newPc && offlineCount < pollTriesBeforeOffline)
                        return false;

                    details.state = ComputerDetails.State.OFFLINE;
                }
            } catch (InterruptedException e) {
                throw e;
            }

            // If it's online, update our persistent state
            if (details.state == ComputerDetails.State.ONLINE) {
                try {
                    // If the active address is a site-local address (RFC 1918), then use STUN to populate the external address field if it's not set already.
                    if (details.remoteAddress == null) {
                        InetAddress addr = InetAddress.getByName(details.activeAddress);
                        if (addr.isSiteLocalAddress()) {
                            populateExternalAddress(details);
                        }
                    }
                } catch (UnknownHostException ignored) {
                }
            }

            // Don't call the listener if this is a failed lookup of a new PC
            //if ((!newPc || details.state == ComputerDetails.State.ONLINE) && listener != null)
            //    listener.onComputerUpdated(details);

            return true;
        }


        //--------SUPPORT FUNCTIONS FOR PLAYING--------

        private String checkPairCondition(ComputerDetails computer, PlayServices.ComputerManagerBinder managerBinder) {
            String err = null;

            //if (computer.state == ComputerDetails.State.OFFLINE || ServerHelper.getCurrentAddressFromComputer(computer) == null)
            //    err = getApplication().getResources().getString(R.string.pair_pc_offline);
            //else
            if (computer.runningGameId != 0)
                err = getApplication().getResources().getString(R.string.pair_pc_ingame);
            else if (managerBinder == null)
                err = getApplication().getResources().getString(R.string.error_manager_not_running);

            if (err != null)
                RoraLog.info("Pairing - " + "Err: " + err);

            return err;
        }

        private void invalidateStateForComputer() {
            // We need the network lock to prevent a concurrent poll from wiping this change out
            synchronized (pollingTuple.networkLock) {
                pollingTuple.computer.state = ComputerDetails.State.UNKNOWN;
            }
        }


        //--------------------- POLLING METHODS ----------------------

        private String fastPollPc(final String localAddress, final String remoteAddress, final String manualAddress, final String ipv6Address) throws InterruptedException {
            final boolean[] remoteInfo = new boolean[2];
            final boolean[] localInfo = new boolean[2];
            final boolean[] manualInfo = new boolean[2];
            final boolean[] ipv6Info = new boolean[2];

            startFastPollThread(localAddress, localInfo);
            startFastPollThread(remoteAddress, remoteInfo);
            startFastPollThread(manualAddress, manualInfo);
            startFastPollThread(ipv6Address, ipv6Info);

            // Check local first
            synchronized (localInfo) {
                while (!localInfo[0]) {
                    localInfo.wait(500);
                }

                if (localInfo[1]) {
                    return localAddress;
                }
            }

            // Now manual
            synchronized (manualInfo) {
                while (!manualInfo[0]) {
                    manualInfo.wait(500);
                }

                if (manualInfo[1]) {
                    return manualAddress;
                }
            }

            // Now remote IPv4
            synchronized (remoteInfo) {
                while (!remoteInfo[0]) {
                    remoteInfo.wait(500);
                }

                if (remoteInfo[1]) {
                    return remoteAddress;
                }
            }

            // Now global IPv6
            synchronized (ipv6Info) {
                while (!ipv6Info[0]) {
                    ipv6Info.wait(500);
                }

                if (ipv6Info[1]) {
                    return ipv6Address;
                }
            }

            return null;
        }

        private void startFastPollThread(final String address, final boolean[] info) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    boolean pollRes = fastPollIp(address);

                    synchronized (info) {
                        info[0] = true; // Done
                        info[1] = pollRes; // Polling result

                        info.notify();
                    }
                }
            };
            t.setName("Fast Poll - " + address);
            t.start();
        }

        private ComputerDetails tryPollIp(ComputerDetails details, String address) {
            // Fast poll this address first to determine if we can connect at the TCP layer
            if (!fastPollIp(address)) {
                return null;
            }

            try {
                NvHTTP http = new NvHTTP(address, idManager.getUniqueId(), details.serverCert, PlatformBinding.getCryptoProvider(PlayServices.this));

                ComputerDetails newDetails = http.getComputerDetails();

                // Check if this is the PC we expected
                if (newDetails.uuid == null) {
                    RoraLog.severe("Adding PC - Polling returned no UUID!");
                    return null;
                }
                // details.uuid can be null on initial PC add
                else if (details.uuid != null && !details.uuid.equals(newDetails.uuid)) {
                    // We got the wrong PC!
                    RoraLog.info("Adding PC - Polling returned the wrong PC!");
                    return null;
                }

                // Set the new active address
                newDetails.activeAddress = address;

                return newDetails;
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Just try to establish a TCP connection to speculatively detect a running GFE server
        private boolean fastPollIp(String address) {
            if (address == null)
                return false;

            Socket s = new Socket();
            try {
                s.connect(new InetSocketAddress(address, NvHTTP.HTTPS_PORT1), FAST_POLL_TIMEOUT);
                s.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        //------------------------------------------------------------


        private void startTupleThread() {
            pollingTuple.thread = createPollingThread(pollingTuple);
            pollingTuple.thread.start();
        }

        private Thread createPollingThread(final PollingTuple tuple) {
            Thread t = new Thread() {
                @Override
                public void run() {

                    int offlineCount = 0;
                    while (!isInterrupted() && (state.getValue() == UserPlayingData.PlayingState.IN_PROGRESS || state.getValue() == UserPlayingData.PlayingState.PLAYING)) {
                        try {
                            // Only allow one request to the machine at a time
                            synchronized (tuple.networkLock) {
                                // Check if this poll has modified the details
                                if (!runPoll(tuple.computer, false, offlineCount)) {
                                    RoraLog.warning(tuple.computer.name + " is offline (try " + offlineCount + ")");
                                    offlineCount++;
                                } else {
                                    tuple.lastSuccessfulPollMs = SystemClock.elapsedRealtime();
                                    offlineCount = 0;
                                }
                            }

                            // Wait until the next polling interval
                            Thread.sleep(SERVERINFO_POLLING_PERIOD_MS);
                        } catch (InterruptedException e) {
                            RoraLog.info("Polling - Err: " + e.getMessage());
                            break;
                        }
                    }
                }
            };
            t.setName("Polling thread for " + tuple.computer.name);
            return t;
        }

        private void populateExternalAddress(ComputerDetails details) {
            boolean boundToNetwork = false;
            boolean activeNetworkIsVpn = NetHelper.isActiveNetworkVpn(PlayServices.this);
            ConnectivityManager connMgr = (ConnectivityManager) PlayServices.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            // Check if we're currently connected to a VPN which may send our
            // STUN request from an unexpected interface
            if (activeNetworkIsVpn) {
                // Acquire the default network lock since we could be changing global process state
                defaultNetworkLock.lock();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // On Lollipop or later, we can bind our process to the underlying interface
                    // to ensure our STUN request goes out on that interface or not at all (which is
                    // preferable to getting a VPN endpoint address back).
                    Network[] networks = connMgr.getAllNetworks();
                    for (Network net : networks) {
                        NetworkCapabilities netCaps = connMgr.getNetworkCapabilities(net);
                        if (netCaps != null) {
                            if (!netCaps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) &&
                                    !netCaps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                                // This network looks like an underlying multicast-capable transport,
                                // so let's guess that it's probably where our mDNS response came from.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (connMgr.bindProcessToNetwork(net)) {
                                        boundToNetwork = true;
                                        break;
                                    }
                                } else if (ConnectivityManager.setProcessDefaultNetwork(net)) {
                                    boundToNetwork = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // Perform the STUN request if we're not on a VPN or if we bound to a network
            if (!activeNetworkIsVpn || boundToNetwork) {
                details.remoteAddress = NvConnection.findExternalAddressForMdns("stun.moonlight-stream.org", 3478);
            }

            // Unbind from the network
            if (boundToNetwork) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    connMgr.bindProcessToNetwork(null);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ConnectivityManager.setProcessDefaultNetwork(null);
                }
            }

            // Unlock the network state
            if (activeNetworkIsVpn) {
                defaultNetworkLock.unlock();
            }
        }

    }

}

class PollingTuple {
    public Thread thread;
    public final ComputerDetails computer;
    public final Object networkLock;
    public long lastSuccessfulPollMs;

    public PollingTuple(ComputerDetails computer, Thread thread) {
        this.computer = computer;
        this.thread = thread;
        this.networkLock = new Object();
    }
}