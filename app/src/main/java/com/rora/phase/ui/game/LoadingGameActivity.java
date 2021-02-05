package com.rora.phase.ui.game;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.rora.phase.AppView;
import com.rora.phase.LimeLog;
import com.rora.phase.R;
import com.rora.phase.binding.PlatformBinding;
import com.rora.phase.binding.crypto.AndroidCryptoProvider;
import com.rora.phase.computers.ComputerManagerListener;
import com.rora.phase.computers.ComputerManagerService;
import com.rora.phase.grid.assets.DiskAssetLoader;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.nvstream.http.NvApp;
import com.rora.phase.nvstream.http.NvHTTP;
import com.rora.phase.nvstream.http.PairingManager;
import com.rora.phase.preferences.GlPreferences;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.utils.Dialog;
import com.rora.phase.utils.ServerHelper;
import com.rora.phase.utils.UiHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LoadingGameActivity extends FragmentActivity {

    private ComputerDetails computer;
    private ComputerManagerService.ApplistPoller poller;
    private ComputerManagerService.ComputerManagerBinder managerBinder;
    private boolean freezeUpdates, runningPolling, inForeground, completeOnCreateCalled;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            final ComputerManagerService.ComputerManagerBinder localBinder = ((ComputerManagerService.ComputerManagerBinder)binder);

            // Wait in a separate thread to avoid stalling the UI
            new Thread() {
                @Override
                public void run() {
                    // Wait for the binder to be ready
                    localBinder.waitForReady();

                    // Now make the binder visible
                    managerBinder = localBinder;

                    // Start updates
                    startComputerUpdates();

                    // Force a keypair to be generated early to avoid discovery delays
                    new AndroidCryptoProvider(LoadingGameActivity.this).getClientCertificate();
                }
            }.start();
        }

        public void onServiceDisconnected(ComponentName className) {
            managerBinder = null;
        }
    };

    public static final String COMPUTER_PARAM = "COMPUTER_PARAM";

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Only reinitialize views if completeOnCreate() was called
        // before this callback. If it was not, completeOnCreate() will
        // handle initializing views with the config change accounted for.
        // This is not prone to races because both callbacks are invoked
        // in the main thread.
        if (completeOnCreateCalled) {
            // Reinitialize views just in case orientation changed
            initializeViews();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_game);

        // Assume we're in the foreground when created to avoid a race
        // between binding to CMS and onResume()
        inForeground = true;

        // Create a GLSurfaceView to fetch GLRenderer unless we have
        // a cached result already.
        final GlPreferences glPrefs = GlPreferences.readPreferences(this);
        if (!glPrefs.savedFingerprint.equals(Build.FINGERPRINT) || glPrefs.glRenderer.isEmpty()) {
            GLSurfaceView surfaceView = new GLSurfaceView(this);
            surfaceView.setRenderer(new GLSurfaceView.Renderer() {
                @Override
                public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                    // Save the GLRenderer string so we don't need to do this next time
                    glPrefs.glRenderer = gl10.glGetString(GL10.GL_RENDERER);
                    glPrefs.savedFingerprint = Build.FINGERPRINT;
                    glPrefs.writePreferences();

                    LimeLog.info("Fetched GL Renderer: " + glPrefs.glRenderer);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            completeOnCreate();
                        }
                    });
                }

                @Override
                public void onSurfaceChanged(GL10 gl10, int i, int i1) {
                }

                @Override
                public void onDrawFrame(GL10 gl10) {
                }
            });
            setContentView(surfaceView);
        }
        else {
            LimeLog.info("Cached GL Renderer: " + glPrefs.glRenderer);
            completeOnCreate();
        }

        computer = new ComputerDetails();
        computer.manualAddress = "171.247.39.92";
        startAddThread(computer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (managerBinder != null) {
            unbindService(serviceConnection);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Display a decoder crash notification if we've returned after a crash
        UiHelper.showDecoderCrashDialog(this);

        inForeground = true;
        startComputerUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();

        inForeground = false;
        stopComputerUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();

        Dialog.closeDialogs();
    }


    private void completeOnCreate() {
        completeOnCreateCalled = true;

        UiHelper.setLocale(this);

        // Bind to the computer manager service
        bindService(new Intent(this, ComputerManagerService.class), serviceConnection, Service.BIND_AUTO_CREATE);

        initializeViews();
    }

    private void initializeViews() {
        UiHelper.notifyNewRootView(this);

        // Set default preferences if we've never been run
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    private void startAddThread(ComputerDetails computerDetails) {
        Thread addThread = new Thread() {
            @Override
            public void run() {
                addPc(computerDetails);
            }
        };
        addThread.setName("UI - AddComputerManually");
        addThread.start();
    }



    //STEP 2: Handle computer details in local - this func stand for AddComputerManually screen
    private void addPc(ComputerDetails computerDetails) {

        boolean wrongSiteLocal = false;
        boolean success;

        try {
            success = managerBinder.addComputerBlocking(computerDetails);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            success = false;
        }
        if (!success){
            //wrongSiteLocal = isWrongSubnetSiteLocalAddress(host);
        }

        //dialog.dismiss();

        if (wrongSiteLocal) {
            //Dialog.displayDialog(this, getResources().getString(R.string.conn_error_title), getResources().getString(R.string.addpc_wrong_sitelocal), false);
        }
        else if (!success) {
            //String dialogText;
            //if (portTestResult != MoonBridge.ML_TEST_RESULT_INCONCLUSIVE && portTestResult != 0)  {
            //    //dialogText = getResources().getString(R.string.nettest_text_blocked);
            //}
            //else {
            //    //dialogText = getResources().getString(R.string.addpc_fail);
            //}
            //Dialog.displayDialog(this, getResources().getString(R.string.conn_error_title), dialogText, false);
        }
        else {
            doPair(computerDetails);
        }
    }

    //STEP 3: Pair computer
    private void doPair(final ComputerDetails computer) {
        if (computer.state == ComputerDetails.State.OFFLINE || ServerHelper.getCurrentAddressFromComputer(computer) == null) {
            Log.d(GameViewModel.class.getSimpleName(), getApplication().getResources().getString(R.string.pair_pc_offline));
            return;
        }
        if (computer.runningGameId != 0) {
            Log.d(GameViewModel.class.getSimpleName(), getApplication().getResources().getString(R.string.pair_pc_ingame));
            return;
        }
        if (managerBinder == null) {
            Log.d(GameViewModel.class.getSimpleName(), getApplication().getResources().getString(R.string.error_manager_not_running));
            return;
        }

        Log.d(GameDetailFragment.class.getSimpleName(), getApplication().getResources().getString(R.string.pairing));
        new Thread(new Runnable() {
            @Override
            public void run() {
                NvHTTP httpConn;
                String message;
                boolean success = false;
                try {
                    // Stop updates and wait while pairing
                    stopComputerUpdates();

                    httpConn = new NvHTTP(ServerHelper.getCurrentAddressFromComputer(computer),
                            managerBinder.getUniqueId(),
                            computer.serverCert,
                            PlatformBinding.getCryptoProvider(getApplication().getApplicationContext()));
                    if (httpConn.getPairState() == PairingManager.PairState.PAIRED) {
                        // Don't display any toast, but open the app list
                        message = null;
                        success = true;
                    }
                    else {
                        final String pinStr = PairingManager.generatePinString();

                        // Spin the dialog off in a thread because it blocks
                        //Dialog.displayDialog(GameDetailFragment.this, getResources().getString(R.string.pair_pairing_title), getResources().getString(R.string.pair_pairing_msg)+" "+pinStr, false);

                        PairingManager pm = httpConn.getPairingManager();

                        PairingManager.PairState pairState = pm.pair(httpConn.getServerInfo(), pinStr);
                        if (pairState == PairingManager.PairState.PIN_WRONG) {
                            message = getApplication().getResources().getString(R.string.pair_incorrect_pin);
                        }
                        else if (pairState == PairingManager.PairState.FAILED) {
                            message = getApplication().getResources().getString(R.string.pair_fail);
                        }
                        else if (pairState == PairingManager.PairState.ALREADY_IN_PROGRESS) {
                            message = getApplication().getResources().getString(R.string.pair_already_in_progress);
                        }
                        else if (pairState == PairingManager.PairState.PAIRED) {
                            // Just navigate to the app view without displaying a toast
                            message = null;
                            success = true;

                            // Pin this certificate for later HTTPS use
                            managerBinder.getComputer(computer.uuid).serverCert = pm.getPairedCert();

                            // Invalidate reachability information after pairing to force
                            // a refresh before reading pair state again
                            managerBinder.invalidateStateForComputer(computer.uuid);
                        }
                        else {
                            // Should be no other values
                            message = null;
                        }
                    }
                } catch (UnknownHostException e) {
                    message = getApplication().getResources().getString(R.string.error_unknown_host);
                } catch (FileNotFoundException e) {
                    message = getApplication().getResources().getString(R.string.error_404);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    message = e.getMessage();
                }

                Dialog.closeDialogs();

                final String toastMessage = message;
                final boolean toastSuccess = success;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toastMessage != null) {
                            Log.d(GameDetailFragment.class.getSimpleName(), toastMessage);
                        }

                        if (toastSuccess) {
                            // Open the app list after a successful pairing attempt
                            //doAppList(computer, true, false);
                            startGame();
                        }
                        else {
                            // Start polling again if we're still in the foreground
                            startComputerUpdates();
                        }
                    }
                });
            }
        }).start();
    }

    //STEP 4: Connect with selected game
    private void startGame() {
        try {

            //STEP 1: Get game data from nvdia
            List<NvApp> list = NvHTTP.getAppListByReader(new StringReader(computer.rawAppList));

            NvApp app = new NvApp();
            app.setAppId(list.get(0).getAppId());
            app.setAppName(list.get(0).getAppName());

            //STEP 2: Start game with app's data
            ServerHelper.doStart(this, app, computer, managerBinder);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void doUnpair(final ComputerDetails computer) {
        if (computer.state == ComputerDetails.State.OFFLINE || ServerHelper.getCurrentAddressFromComputer(computer) == null) {
            Toast.makeText(this, getResources().getString(R.string.error_pc_offline), Toast.LENGTH_SHORT).show();
            return;
        }
        if (managerBinder == null) {
            Toast.makeText(this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, getResources().getString(R.string.unpairing), Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                NvHTTP httpConn;
                String message;
                try {
                    httpConn = new NvHTTP(ServerHelper.getCurrentAddressFromComputer(computer),
                            managerBinder.getUniqueId(),
                            computer.serverCert,
                            PlatformBinding.getCryptoProvider(getApplicationContext()));
                    if (httpConn.getPairState() == PairingManager.PairState.PAIRED) {
                        httpConn.unpair();
                        if (httpConn.getPairState() == PairingManager.PairState.NOT_PAIRED) {
                            message = getResources().getString(R.string.unpair_success);
                        }
                        else {
                            message = getResources().getString(R.string.unpair_fail);
                        }
                    }
                    else {
                        message = getResources().getString(R.string.unpair_error);
                    }
                } catch (UnknownHostException e) {
                    message = getResources().getString(R.string.error_unknown_host);
                } catch (FileNotFoundException e) {
                    message = getResources().getString(R.string.error_404);
                } catch (XmlPullParserException | IOException e) {
                    message = e.getMessage();
                    e.printStackTrace();
                }

                final String toastMessage = message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    private void startComputerUpdates() {
        // Don't start polling if we're not bound or in the foreground
        if (managerBinder == null || !inForeground) {
            return;
        }

        managerBinder.startPolling(new ComputerManagerListener() {
            @Override
            public void notifyComputerUpdated(final ComputerDetails details) {

                if (details.state == ComputerDetails.State.OFFLINE) {
                    // The PC is unreachable now
                    LoadingGameActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Display a toast to the user and quit the activity
                            Toast.makeText(LoadingGameActivity.this, getResources().getText(R.string.lost_connection), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    return;
                }

                // Close immediately if the PC is no longer paired
                if (details.state == ComputerDetails.State.ONLINE && details.pairState != PairingManager.PairState.PAIRED) {
                    LoadingGameActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Display a toast to the user and quit the activity
                            Toast.makeText(LoadingGameActivity.this, getResources().getText(R.string.scut_not_paired), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    return;
                }
            }
        });

        if (poller == null) {
            poller = managerBinder.createAppListPoller(computer);
        }
        poller.start();
    }

    private void stopComputerUpdates() {
        if (poller != null) {
            poller.stop();
        }

        if (managerBinder != null) {
            managerBinder.stopPolling();
        }
    }

}