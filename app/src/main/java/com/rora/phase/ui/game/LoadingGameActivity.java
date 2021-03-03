package com.rora.phase.ui.game;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.rora.phase.LimeLog;
import com.rora.phase.R;
import com.rora.phase.binding.crypto.AndroidCryptoProvider;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.nvstream.http.PairingManager;
import com.rora.phase.preferences.GlPreferences;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.utils.Dialog;
import com.rora.phase.utils.UiHelper;
import com.rora.phase.utils.callback.PlayGameProgressCallBack;
import com.rora.phase.utils.services.PlayServices;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LoadingGameActivity extends FragmentActivity {

    private ProgressBar pbLoadingProgress;

    private GameViewModel gameViewModel;
    private boolean inForeground, completeOnCreateCalled;
    private PlayServices.ComputerManagerBinder managerBinder;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            final PlayServices.ComputerManagerBinder localBinder = ((PlayServices.ComputerManagerBinder)binder);

            // Wait in a separate thread to avoid stalling the UI
            new Thread() {
                @Override
                public void run() {
                    // Wait for the binder to be ready
                    localBinder.waitForReady();

                    // Now make the binder visible
                    managerBinder = localBinder;

                    // Force a keypair to be generated early to avoid discovery delays
                    new AndroidCryptoProvider(LoadingGameActivity.this).getClientCertificate();

                    if (managerBinder.isStopPlaying())
                        stopConnect();
                    else
                        startConnect();
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


    //--------------------------- LIFECYCLE -----------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_game);
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // Assume we're in the foreground when created to avoid a race between binding to CMS and onResume()
        inForeground = true;

        pbLoadingProgress = findViewById(R.id.loading_game_pb);

        // Create a GLSurfaceView to fetch GLRenderer unless we have a cached result already.
        final GlPreferences glPrefs = GlPreferences.readPreferences(this);
        if (!gameViewModel.isStopPlaying() && !glPrefs.savedFingerprint.equals(Build.FINGERPRINT) || glPrefs.glRenderer.isEmpty()) {
            GLSurfaceView surfaceView = new GLSurfaceView(this);
            surfaceView.setRenderer(new GLSurfaceView.Renderer() {
                @Override
                public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                    // Save the GLRenderer string so we don't need to do this next time
                    glPrefs.glRenderer = gl10.glGetString(GL10.GL_RENDERER);
                    glPrefs.savedFingerprint = Build.FINGERPRINT;
                    glPrefs.writePreferences();

                    LimeLog.info("Fetched GL Renderer: " + glPrefs.glRenderer);

                    runOnUiThread(() -> completeOnCreate());
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (managerBinder != null)
            stopConnect();
    }

    //-------------------------------------------------------------------------


    private void completeOnCreate() {
        completeOnCreateCalled = true;

        UiHelper.setLocale(this);

        // Bind to the computer manager service
        bindService(new Intent(this, PlayServices.class), serviceConnection, Service.BIND_AUTO_CREATE);

        initializeViews();
    }

    private void initializeViews() {
        UiHelper.notifyNewRootView(this);

        // Set default preferences if we've never been run
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    private void startConnect() {
        Bundle bundle = getIntent().getBundleExtra("LoadingGameActivityBundle");
        ComputerDetails computer = (ComputerDetails) bundle.getSerializable(COMPUTER_PARAM);

        Thread connectThread = new Thread(() -> {
            managerBinder.startConnectProgress(this, computer, playProgressCallBack);
        });
        connectThread.setName("UI - LoadingGameActivity");
        connectThread.start();
    }

    private void stopConnect() {
        new Thread(() -> {
            managerBinder.stopConnect(playProgressCallBack);
        }).start();
        unbindService(serviceConnection);
    }

    private void startComputerUpdates() {
        LimeLog.info("Start updating computer");
        if (managerBinder == null || !inForeground)
            return;

        managerBinder.startPolling(details -> {
            // The PC is unreachable now, quit the activity
            if (details.state == ComputerDetails.State.OFFLINE) {
                LoadingGameActivity.this.runOnUiThread(() -> {
                    LimeLog.info(getResources().getString(R.string.lost_connection) + " with " + details.toString());
                    finish();
                });

                return;
            }

            // Close immediately if the PC is no longer paired
            if (details.state == ComputerDetails.State.ONLINE && details.pairState != PairingManager.PairState.PAIRED) {
                LoadingGameActivity.this.runOnUiThread(() -> {
                    LimeLog.info(getResources().getString(R.string.scut_not_paired) + " with " + details.toString());
                    finish();
                });

                return;
            }
        });
    }

    private void stopComputerUpdates() {
        if (managerBinder == null)
            return;

        LimeLog.info("Stop updating computer");
        managerBinder.stopPolling();
    }

    // HANDLE PLAY PROGRESS
    private final PlayGameProgressCallBack playProgressCallBack =  new PlayGameProgressCallBack() {
        @Override
        public void onAddPc(boolean isDone) {
            pbLoadingProgress.setProgress(isDone ? 1 : 2);
        }

        @Override
        public void onPairPc(boolean isDone) {
            pbLoadingProgress.setProgress(isDone ? 3 : 4);
        }

        @Override
        public void onStartConnect(boolean isDone) {
            pbLoadingProgress.setProgress(isDone ? 5 : 6);
        }

        @Override
        public void onStopConnect(boolean isDone) {
            stopComputerUpdates();
            LoadingGameActivity.this.finish();
        }

        @Override
        public void onError(String err) {
            LoadingGameActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show());
            LoadingGameActivity.this.finish();
        }
    };

}