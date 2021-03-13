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

import com.rora.phase.LimeLog;
import com.rora.phase.R;
import com.rora.phase.binding.PlatformBinding;
import com.rora.phase.computers.IdentityManager;
import com.rora.phase.discovery.DiscoveryService;
import com.rora.phase.model.UserPlayingData;
import com.rora.phase.nvstream.NvConnection;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.nvstream.http.NvApp;
import com.rora.phase.nvstream.http.NvHTTP;
import com.rora.phase.nvstream.http.PairingManager;
import com.rora.phase.nvstream.jni.MoonBridge;
import com.rora.phase.nvstream.mdns.MdnsComputer;
import com.rora.phase.nvstream.mdns.MdnsDiscoveryListener;
import com.rora.phase.repository.GameRepository;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.ui.settings.auth.SignInActivity;
import com.rora.phase.utils.NetHelper;
import com.rora.phase.utils.ServerHelper;
import com.rora.phase.utils.callback.PlayGameProgressCallBack;
import com.rora.phase.utils.network.realtime.playhub.PlayHub;
import com.rora.phase.utils.network.realtime.playhub.PlayHubListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PlayServices extends Service {
    private final PlayServices.ComputerManagerBinder binder = new PlayServices.ComputerManagerBinder();
    private final ComputerServices computerServices = new ComputerServices();

    private UserPlayingData.PlayingState state = UserPlayingData.PlayingState.IDLE;
    private PollingTuple pollingTuple;
    private PlayGameProgressCallBack listener = null;
    private IdentityManager idManager;
    UserRepository userRepository;
    private PlayHub playHub;

    private DiscoveryService.DiscoveryBinder discoveryBinder;
    private final ServiceConnection discoveryServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            synchronized (discoveryServiceConnection) {
                DiscoveryService.DiscoveryBinder privateBinder = ((DiscoveryService.DiscoveryBinder)binder);

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
        bindService(new Intent(this, DiscoveryService.class), discoveryServiceConnection, Service.BIND_AUTO_CREATE);

        // Lookup or generate this device's UID
        idManager = new IdentityManager(this);
        userRepository = new UserRepository(getApplicationContext());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (state == UserPlayingData.PlayingState.IN_PROGRESS || state == UserPlayingData.PlayingState.PLAYING) {
            stopConnect(null);
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
                    LimeLog.warning("Auto-discovered PC failed to respond: "+details);
                }
            }

            @Override
            public void notifyComputerRemoved(MdnsComputer computer) {
                // Nothing to do here
            }

            @Override
            public void notifyDiscoveryFailure(Exception e) {
                LimeLog.severe("mDNS discovery failed");
                e.printStackTrace();
            }
        };
    }


    //====================== PLAYING GAME STEPS =========================

    /** Start the progress after getting ip and ports from STEP 1 */
    public void startConnectProgress(Activity activity, PlayGameProgressCallBack callBack) {
        callBack.onStart(false);
        playHub = new PlayHub();
        //STEP 1: Connect to hub
        playHub.startConnect(getApplicationContext(), new PlayHubListener() {
            @Override
            public void onConnected() {
                callBack.onStart(true);
                callBack.onFindAHost(false);
                //STEP 2: Get host data
                //gameRepository.getComputerIPData((errMsg, computer) -> {
                //    callBack.onFindAHost(true);
                //    if (errMsg != null) {
                //        playHub.stopConnect();
                //        callBack.onError(errMsg);
                //        return;
                //    }
                //
                //    try {
                //        //STEP 3: Add pc
                //        callBack.onAddPc(false);
                //        String err = addPc(computer);
                //        if (err != null) {
                //            callBack.onError(err);
                //            return;
                //        }
                //        callBack.onAddPc(true);
                //        Thread.sleep(1000);
                //
                //        //STEP 4: Pair
                //        callBack.onPairPc(false);
                //        err = startPairing();
                //        if (err != null) {
                //            if (err.equals(getApplication().getResources().getString(R.string.pair_pc_ingame)))
                //                stopConnect(callBack);
                //            callBack.onError(err);
                //            return;
                //        }
                //        callBack.onPairPc(true);
                //        Thread.sleep(1000);
                //
                //        //STEP 5: Play
                //        callBack.onStartConnect(false);
                //        start(activity, binder);
                //        callBack.onStartConnect(true);
                //    } catch (InterruptedException e) {
                //        LimeLog.info("Playing Game - Connecting error -- " + e.getMessage());
                //        callBack.onError(getApplication().getResources().getString(R.string.undetected_error));
                //    }
                //});
            }

            @Override
            public void onDisconnected() {

            }
        });
    }

    /** STEP 3: Connect to pc
     *
     * - Try connect to pc and get its state
     *
     * @return An error msg if failed
     * */
    private String addPc(ComputerDetails computerDetails) {
        LimeLog.info("Playing game - STEP 2: Add computer -- " + computerDetails.toString());
        String err = null;
        try {
            boolean success = computerServices.addComputerBlocking(computerDetails);
            if(!success)
                err = getApplication().getResources().getString(R.string.undetected_error);
        } catch (IllegalArgumentException e) {
            err = e.getMessage();
        }

        LimeLog.info("Add pc - " + (err == null ? "Success" : ("Error: " + handleAddPcErr(computerDetails.manualAddress))));
        return err;
    }

    /** STEP 4: Pair computer
     *
     * - Send paring pin to server and try to pair with computer
     * Use this method in a thread
     *
     * @return An error msg if failed
     * */
    private String startPairing() {
        LimeLog.info("Playing game - STEP 3: Start pairing");
        String err = checkPairCondition(pollingTuple.computer, binder);

        try {
            if(err != null)
                return err;

            NvHTTP httpConn = new NvHTTP(ServerHelper.getCurrentAddressFromComputer(pollingTuple.computer),
                    binder.getUniqueId(),
                    pollingTuple.computer.serverCert,
                    PlatformBinding.getCryptoProvider(getApplication().getApplicationContext()));

            if (httpConn.getPairState() == PairingManager.PairState.PAIRED)
                return null;

            //STEP 3.1: GENERATE AND WAITING FOR SERVER HANDEL PIN
            final String pinStr = PairingManager.generatePinString();

            //STEP 3.2: SEND PIN TO HOST
            LimeLog.info("Pairing - Waiting for pin confirmation: " + pinStr);
            userRepository.sendPinToHost(pinStr);

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
                    invalidateStateForComputer();
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

        LimeLog.info("Pairing - " + (err == null ?  "Success" : "Err: " + err));
        return err;
    }

    /** STEP 5: Start remote connect
     * */
    private void start(Activity activity, PlayServices.ComputerManagerBinder managerBinder) {
        LimeLog.info("Playing game - STEP 4: Start remote connect");
        ServerHelper.doStart(activity, NvApp.initRemoteApp(), pollingTuple.computer, managerBinder);
    }

    /** STEP 6 - FINAL: Counting playtime
     * */
    public void startCountingPlaytime() {

    }

    /** Use this method in a thread
     * @return An error msg if failed
     * */
    public void stopConnect(PlayGameProgressCallBack playProgressCallBack) {
        playProgressCallBack.onStopConnect(false);
        if (pollingTuple == null || pollingTuple.computer == null) {
            LimeLog.info("Stop connect - Error: Couldn't find local data");
            return;
        }

        state = UserPlayingData.PlayingState.IDLE;
        pollingTuple = null;
        listener = null;
        unbindService(discoveryServiceConnection);
        bindService(new Intent(this, DiscoveryService.class), discoveryServiceConnection, Service.BIND_AUTO_CREATE);
        userRepository.stopPlaying();

        LimeLog.info("Stop connect - Success");
        playProgressCallBack.onStopConnect(true);
    }

    //===================================================================


    //--------SUPPORT FUNCTIONS FOR PLAYING--------

    private String handleAddPcErr(String manualAddress) {
        String err;
        boolean wrongSiteLocal;
        int portTestResult;

        wrongSiteLocal = isWrongSubnetSiteLocalAddress(manualAddress);

        if (!wrongSiteLocal)
            // Run the test before dismissing the spinner because it can take a few seconds.
            portTestResult = MoonBridge.testClientConnectivity(ServerHelper.CONNECTION_TEST_SERVER, 443,MoonBridge.ML_PORT_FLAG_TCP_47984 | MoonBridge.ML_PORT_FLAG_TCP_47989, NvHTTP.HTTPS_PORT1);
        else
            // Don't bother with the test if we succeeded or the IP address was bogus
            portTestResult = MoonBridge.ML_TEST_RESULT_INCONCLUSIVE;

        if (wrongSiteLocal)
            err = getApplication().getResources().getString(R.string.addpc_wrong_sitelocal);
        else {
            if (portTestResult != MoonBridge.ML_TEST_RESULT_INCONCLUSIVE && portTestResult != 0)
                err = getApplication().getResources().getString(R.string.nettest_text_blocked);
            else
                err = getApplication().getResources().getString(R.string.addpc_fail);
        }

        return getApplication().getResources().getString(R.string.conn_error_title) + " -- " + err;
    }

    private boolean isWrongSubnetSiteLocalAddress(String address) {
        try {
            InetAddress targetAddress = InetAddress.getByName(address);
            if (!(targetAddress instanceof Inet4Address) || !targetAddress.isSiteLocalAddress()) {
                return false;
            }

            // We have a site-local address. Look for a matching local interface.
            for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InterfaceAddress addr : iface.getInterfaceAddresses()) {
                    if (!(addr.getAddress() instanceof Inet4Address) || !addr.getAddress().isSiteLocalAddress()) {
                        // Skip non-site-local or non-IPv4 addresses
                        continue;
                    }

                    byte[] targetAddrBytes = targetAddress.getAddress();
                    byte[] ifaceAddrBytes = addr.getAddress().getAddress();

                    // Compare prefix to ensure it's the same
                    boolean addressMatches = true;
                    for (int i = 0; i < addr.getNetworkPrefixLength(); i++) {
                        if ((ifaceAddrBytes[i / 8] & (1 << (i % 8))) != (targetAddrBytes[i / 8] & (1 << (i % 8)))) {
                            addressMatches = false;
                            break;
                        }
                    }

                    if (addressMatches) {
                        return false;
                    }
                }
            }

            // Couldn't find a matching interface
            return true;
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String checkPairCondition(ComputerDetails computer, PlayServices.ComputerManagerBinder managerBinder) {
        String err = null;

        if (computer.state == ComputerDetails.State.OFFLINE || ServerHelper.getCurrentAddressFromComputer(computer) == null)
            err = getApplication().getResources().getString(R.string.pair_pc_offline);
        else if (computer.runningGameId != 0)
            err = getApplication().getResources().getString(R.string.pair_pc_ingame);
        else if (managerBinder == null)
            err = getApplication().getResources().getString(R.string.error_manager_not_running);

        if (err != null)
            LimeLog.info("Pairing - " + "Err: " + err);

        return err;
    }

    private void invalidateStateForComputer() {
        // We need the network lock to prevent a concurrent poll from wiping this change out
        synchronized (pollingTuple.networkLock) {
            pollingTuple.computer.state = ComputerDetails.State.UNKNOWN;
        }
    }

    private boolean isStopPlaying() {
        return state == UserPlayingData.PlayingState.STOP;
    }

    private void updatePlayState(UserPlayingData.PlayingState state) {
        this.state = state;
    }


    //--------------------------------- Classes -------------------------------

    public class ComputerManagerBinder extends Binder {
        private static final int POLL_DATA_TTL_MS = 30000;
        private static final int MDNS_QUERY_PERIOD_MS = 1000;

        public void startConnectProgress(Activity activity, PlayGameProgressCallBack playProgressCallBack) {
            PlayServices.this.startConnectProgress(activity, playProgressCallBack);
        }

        public void stopConnect(PlayGameProgressCallBack playProgressCallBack) {
            PlayServices.this.stopConnect(playProgressCallBack);
        }

        public void startPolling(PlayGameProgressCallBack listener) {
            if (pollingTuple == null)
                return;

            LimeLog.info("Start updating computer");
            PlayServices.this.listener = listener;

            // Start mDNS auto discovery too
            discoveryBinder.startDiscovery(MDNS_QUERY_PERIOD_MS);

            // Enforce the poll data TTL
            if (SystemClock.elapsedRealtime() - pollingTuple.lastSuccessfulPollMs > POLL_DATA_TTL_MS) {
                LimeLog.info("Timing out polled state for "+pollingTuple.computer.name);
                pollingTuple.computer.state = ComputerDetails.State.UNKNOWN;
            }

            // Report this computer initially
            listener.onComputerUpdated(pollingTuple.computer);
        }

        public void stopPolling() {
            LimeLog.info("Stop polling/updating computer");

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
            return  PlayServices.this.isStopPlaying();
        }

    }

    private class ComputerServices {
        private final Lock defaultNetworkLock = new ReentrantLock();

        private static final int FAST_POLL_TIMEOUT = 1000;
        private static final int OFFLINE_POLL_TRIES = 5;
        private static final int INITIAL_POLL_TRIES = 2;
        private static final int SERVERINFO_POLLING_PERIOD_MS = 1500;

        /** Fill the necessary computer details
         *
         * @return true if success*/
        public boolean addComputerBlocking(ComputerDetails fakeDetails) {
            // Block while we try to fill the details
            try {
                // We cannot use runPoll() here because it will attempt to persist the state of the machine
                // in the database, which would be bad because we don't have our pinned cert loaded yet.
                pollingTuple = new PollingTuple(fakeDetails, null);
                NvHTTP.HTTPS_PORT1 = fakeDetails.httpsPort1;
                NvHTTP.HTTP_PORT2 = fakeDetails.httpsPort1+1;
                if (pollComputer(pollingTuple.computer)) {
                    // Poll again, possibly with the pinned cert, to get accurate pairing information.
                    runPoll(pollingTuple.computer, true, 0);

                    // If the machine is reachable, it was successful
                    if (pollingTuple.computer.state == ComputerDetails.State.ONLINE) {
                        LimeLog.info("Adding PC - New PC ("+pollingTuple.computer.name+") is UUID "+pollingTuple.computer.uuid);

                        // Start a polling thread for this machine
                        startTupleThread();
                        return true;
                    }
                    else return false;
                }
            } catch (InterruptedException e) {
                return false;
            }

            return false;
        }

        /** Do a TCP-level connection to the HTTP server to see if it's listening.
         *
         * @return true if success*/
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
                LimeLog.info("Adding PC - Couldn't connect to this IP address " + details.manualAddress);
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
            }
            else {
                return false;
            }
        }

        /** Make a listener if computer was modified
         *
         * @return true if it was modified */
        private boolean runPoll(ComputerDetails details, boolean newPc, int offlineCount) throws InterruptedException {
            if (isStopPlaying()) {
                stopConnect(null);
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
                } catch (UnknownHostException ignored) {}
            }

            // Don't call the listener if this is a failed lookup of a new PC
            if ((!newPc || details.state == ComputerDetails.State.ONLINE) && listener != null)
                listener.onComputerUpdated(details);

            return true;
        }

        public void removeComputer() {
            if (pollingTuple.thread != null) {
                // Interrupt the thread on this entry
                pollingTuple.thread.interrupt();
                pollingTuple.thread = null;
            }
            pollingTuple = null;
        }

        //-------------------------- SUPPORT METHODS ----------------------------

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
            t.setName("Fast Poll - "+address);
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
                    LimeLog.severe("Adding PC - Polling returned no UUID!");
                    return null;
                }
                // details.uuid can be null on initial PC add
                else if (details.uuid != null && !details.uuid.equals(newDetails.uuid)) {
                    // We got the wrong PC!
                    LimeLog.info("Adding PC - Polling returned the wrong PC!");
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
                    while (!isInterrupted() && (state == UserPlayingData.PlayingState.IN_PROGRESS || state == UserPlayingData.PlayingState.PLAYING)) {
                        try {
                            // Only allow one request to the machine at a time
                            synchronized (tuple.networkLock) {
                                // Check if this poll has modified the details
                                if (!runPoll(tuple.computer, false, offlineCount)) {
                                    LimeLog.warning(tuple.computer.name + " is offline (try " + offlineCount + ")");
                                    offlineCount++;
                                } else {
                                    tuple.lastSuccessfulPollMs = SystemClock.elapsedRealtime();
                                    offlineCount = 0;
                                }
                            }

                            // Wait until the next polling interval
                            Thread.sleep(SERVERINFO_POLLING_PERIOD_MS);
                        } catch (InterruptedException e) {
                            LimeLog.info("Polling - Err: " + e.getMessage());
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
                                }
                                else if (ConnectivityManager.setProcessDefaultNetwork(net)) {
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
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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