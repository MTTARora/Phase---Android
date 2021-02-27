package com.rora.phase.ui.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.LimeLog;
import com.rora.phase.R;
import com.rora.phase.binding.PlatformBinding;
import com.rora.phase.model.Game;
import com.rora.phase.model.UserPlayingData;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.nvstream.http.NvApp;
import com.rora.phase.nvstream.http.NvHTTP;
import com.rora.phase.nvstream.http.PairingManager;
import com.rora.phase.nvstream.jni.MoonBridge;
import com.rora.phase.repository.GameRepository;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.utils.ServerHelper;
import com.rora.phase.utils.callback.PlayGameProgressCallBack;
import com.rora.phase.utils.services.ComputerService;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;

import static com.rora.phase.model.UserPlayingData.PlayingState.IDLE;
import static com.rora.phase.model.UserPlayingData.PlayingState.IN_PROGRESS;

public class GameViewModel extends AndroidViewModel {

    private GameRepository gameRepository;
    private UserRepository userRepository;
    private MutableLiveData<Game> game;
    private MutableLiveData<ComputerDetails> computerDetails;

    public GameViewModel(Application application) {
        super(application);
        gameRepository = new GameRepository();
        userRepository = new UserRepository(application.getBaseContext());

        game = gameRepository.getSelectedGame();
        computerDetails = gameRepository.getComputer();
    }

    public LiveData<Game> getGameData() {
        return game;
    }

    public LiveData<ComputerDetails> getComputerDetails() {
        return computerDetails;
    }

    public void getGame(String gameId) {
        gameRepository.getGameData(gameId);
    }

    /** STEP 1: Get computer ip from server
     * */
    public void getComputerDetailsData() {
        //if (userRepository.isUserLogged())
        gameRepository.getComputerIPData();
        //else
        //    getApplication().getResources().getString(R.string.require_login_msg);
    }

    private void sendPinToHost(String pinStr) {
        gameRepository.sendPinToHost(pinStr);
    }


    //====================== PLAYING GAME STEPS =========================

    /** Start the progress after getting ip from STEP 1 */
    public void startConnectProgress(Activity activity, ComputerService.ComputerManagerBinder managerBinder, ComputerDetails computer, PlayGameProgressCallBack callBack) {
        try {
            //STEP 2: Add pc
            updatePlayState(IN_PROGRESS);
            callBack.onAddPc(false);
            String err = addPc(managerBinder, computer);
            if (err != null) {
                callBack.onError(err);
                return;
            }
            activity.runOnUiThread(() -> computerDetails.setValue(computer));
            callBack.onAddPc(true);
            Thread.sleep(1000);

            //STEP 3: Pair
            callBack.onPairPc(false);
            err = startPairing(managerBinder);
            if (err != null) {
                if (err.equals(getApplication().getResources().getString(R.string.pair_pc_ingame)))
                    stopConnect(managerBinder, callBack);
                callBack.onError(err);
                return;
            }
            activity.runOnUiThread(() -> computerDetails.setValue(computer));
            userRepository.saveLocalUserComputer(computerDetails.getValue());
            callBack.onPairPc(true);
            Thread.sleep(1000);

            //STEP 4: Play
            callBack.onStartConnect(false);
            start(activity, managerBinder);
            callBack.onStartConnect(true);
        } catch (InterruptedException e) {
            LimeLog.info("Playing Game - Connecting error -- " + e.getMessage());
            resetLocalPlayingData();
            callBack.onError(getApplication().getResources().getString(R.string.undetected_error));
        }
    }

    /** STEP 2: Connect to pc
     *
     * - Try connect to pc and get its state
     *
     * @return An error msg if failed
     * */
    private String addPc(ComputerService.ComputerManagerBinder managerBinder, ComputerDetails computerDetails) {
        LimeLog.info("Playing game - STEP 2: Add computer -- " + computerDetails.toString());
        String err = null;
        try {
            boolean success = managerBinder.addComputerBlocking(computerDetails);
            if(!success)
                err = getApplication().getResources().getString(R.string.undetected_error);
        } catch (IllegalArgumentException e) {
            err = e.getMessage();
        }

        LimeLog.info("Add pc - " + (err == null ? "Success" : ("Error: " + handleAddPcErr(computerDetails.manualAddress))));
        return err;
    }

    /** STEP 3: Pair computer
     *
     * - Send paring pin to server and try to pair with computer
     * Use this method in a thread
     *
     * @return An error msg if failed
     * */
    private String startPairing(ComputerService.ComputerManagerBinder managerBinder) {
        ComputerDetails computer = computerDetails.getValue();

        LimeLog.info("Playing game - STEP 3: Start pairing");
        String err = checkPairCondition(computer, managerBinder);

        try {
            if(err != null)
                return err;

            NvHTTP httpConn = new NvHTTP(ServerHelper.getCurrentAddressFromComputer(computer),
                    managerBinder.getUniqueId(),
                    computer.serverCert,
                    PlatformBinding.getCryptoProvider(getApplication().getApplicationContext()));

            if (httpConn.getPairState() == PairingManager.PairState.PAIRED)
                return null;

            //STEP 3.1: GENERATE AND SEND PIN TO HOST
            final String pinStr = PairingManager.generatePinString();
            LimeLog.info("Pairing - Waiting for pairing with pin: " + pinStr);

            sendPinToHost(pinStr);

            //STEP 3.2: Waiting for host handle pin
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
                    managerBinder.getComputer(computer.uuid).serverCert = pm.getPairedCert();
                    computerDetails.getValue().serverCert = pm.getPairedCert();

                    // Invalidate reachability information after pairing to force a refresh before reading pair state again
                    managerBinder.invalidateStateForComputer(computer.uuid);
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

    /** STEP 4: Start remote connect
     * */
    private void start(Activity activity, ComputerService.ComputerManagerBinder managerBinder) {
        LimeLog.info("Playing game - STEP 4: Start remote connect");
        ServerHelper.doStart(activity, NvApp.initRemoteApp(), computerDetails.getValue(), managerBinder);
    }

    /** STEP 5 - FINAL: Counting playtime
     * */
    public void startCountingPlaytime() {

    }

    /** Use this method in a thread
     * @return An error msg if failed
     * */
    public void stopConnect(ComputerService.ComputerManagerBinder managerBinder, PlayGameProgressCallBack playProgressCallBack) {
        NvHTTP httpConn;
        String err = null;
        ComputerDetails computer = userRepository.getLocalUserComputer();
        if (computer == null) {
            err = "UnPair - Error: Couldn't find local data";
            LimeLog.info(err);
            return;
        }

        if (computer.state == ComputerDetails.State.OFFLINE || ServerHelper.getCurrentAddressFromComputer(computer) == null)
            err = getApplication().getResources().getString(R.string.error_pc_offline);

        if (err == null && managerBinder == null)
            err = getApplication().getResources().getString(R.string.error_manager_not_running);

        if (err == null) {
            try {
                httpConn = new NvHTTP(ServerHelper.getCurrentAddressFromComputer(computer),
                        managerBinder.getUniqueId(),
                        computer.serverCert,
                        PlatformBinding.getCryptoProvider(getApplication().getApplicationContext()));
                if (httpConn.getPairState() == PairingManager.PairState.PAIRED) {
                    httpConn.unpair();
                    err = httpConn.getPairState() == PairingManager.PairState.NOT_PAIRED ? null : getApplication().getResources().getString(R.string.unpair_fail);
                }
                else
                    err = getApplication().getResources().getString(R.string.unpair_error);
            } catch (UnknownHostException e) {
                err = getApplication().getResources().getString(R.string.error_unknown_host);
            } catch (FileNotFoundException e) {
                err = getApplication().getResources().getString(R.string.error_404);
            } catch (XmlPullParserException | IOException e) {
                err = e.getMessage();
                e.printStackTrace();
            }
        }

        LimeLog.info("UnPair - " + (err == null ? "Success" : ("Error: " + err)));
        if (err != null)
            playProgressCallBack.onError(err);
        else {
            resetLocalPlayingData();
            LimeLog.info("Stop connect - Success");
            playProgressCallBack.onStopConnect(true);
        }

        return;
    }


    //--------SUPPORT FUNCTIONS FOR PLAYING--------

    private String handleAddPcErr(String manualAddress) {
        ComputerDetails computer = computerDetails.getValue();
        String err;
        boolean wrongSiteLocal;
        int portTestResult;

        wrongSiteLocal = isWrongSubnetSiteLocalAddress(manualAddress);

        if (!wrongSiteLocal)
            // Run the test before dismissing the spinner because it can take a few seconds.
            portTestResult = MoonBridge.testClientConnectivity(ServerHelper.CONNECTION_TEST_SERVER, 443,MoonBridge.ML_PORT_FLAG_TCP_47984 | MoonBridge.ML_PORT_FLAG_TCP_47989);
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

    private String checkPairCondition(ComputerDetails computer, ComputerService.ComputerManagerBinder managerBinder) {
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

    public void updatePlayState(UserPlayingData.PlayingState state) {
        userRepository.savePlayState(state);
    }

    public void resetLocalPlayingData() {
        updatePlayState(IDLE);
        userRepository.saveLocalUserComputer(null);
    }

    //--------------------------------------------

    public boolean isStopPlaying() {
        return userRepository.isStopPlaying();
    }

}
