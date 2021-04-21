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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.rora.phase.RoraLog;
import com.rora.phase.R;
import com.rora.phase.binding.crypto.AndroidCryptoProvider;
import com.rora.phase.model.Game;
import com.rora.phase.model.UserPlayingData;
import com.rora.phase.preferences.GlPreferences;
import com.rora.phase.utils.Dialog;
import com.rora.phase.utils.UiHelper;
import com.rora.phase.utils.callback.PlayGameProgressCallBack;
import com.rora.phase.utils.services.PlayServices;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.rora.phase.ui.game.GameDetailFragment.KEY_GAME;

public class LoadingGameActivity extends FragmentActivity {

    private TextView tvLoadingProgress;
    private ProgressBar pbLoadingProgress;

    private boolean completeOnCreateCalled;

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

                    if (!managerBinder.isStopPlaying())
                        startConnect();
                }
            }.start();
        }

        public void onServiceDisconnected(ComponentName className) {
            managerBinder = null;
        }
    };

    //public static final String COMPUTER_PARAM = "COMPUTER_PARAM";

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

        tvLoadingProgress = findViewById(R.id.loading_game_progress_tv);
        pbLoadingProgress = findViewById(R.id.loading_game_pb);

        // Create a GLSurfaceView to fetch GLRenderer unless we have a cached result already.
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

                    RoraLog.info("Fetched GL Renderer: " + glPrefs.glRenderer);

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
            RoraLog.info("Cached GL Renderer: " + glPrefs.glRenderer);
            completeOnCreate();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (managerBinder != null) {
            pbLoadingProgress.setMax(1);
            stopConnect();
        }
    }

    @Override
    public void onBackPressed() {
        Dialog.displayDialog(this, getResources().getString(R.string.stop_playing_msg) + "?", null, "Yes", "No", new Runnable() {
            @Override
            public void run() {
                if (managerBinder != null && managerBinder.getCurrentState() != UserPlayingData.PlayingState.IN_QUEUE) {
                    stopConnect();
                }
                LoadingGameActivity.super.onBackPressed();
            }
        }, null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
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
        Game game =  Game.fromJson((getIntent().getStringExtra(KEY_GAME)));
        managerBinder.startConnectProgress(this, game, playProgressCallBack);
    }

    private void stopConnect() {
        new Thread(() -> managerBinder.stopConnect(playProgressCallBack)).start();
        unbindService(serviceConnection);
    }

    // HANDLE PLAY PROGRESS
    private final PlayGameProgressCallBack playProgressCallBack =  new PlayGameProgressCallBack() {
        @Override
        public void onStart(boolean isDone) {
            LoadingGameActivity.this.runOnUiThread(() -> {
                tvLoadingProgress.setText(getResources().getString(R.string.getting_data_from_server_play_msg));
                pbLoadingProgress.setProgress(isDone ? 1 : 2);
            });
        }

        @Override
        public void onFindAHost(boolean isDone) {
            LoadingGameActivity.this.runOnUiThread(() -> {
                tvLoadingProgress.setText(getResources().getString(R.string.finding_a_host_play_msg));
                pbLoadingProgress.setProgress(isDone ? 3 : 4);
            });
        }

        @Override
        public void onQueueUpdated(boolean isFirstInit, int total, int position) {
            LoadingGameActivity.this.finish();
        }

        @Override
        public void onPairPc(boolean isDone) {
            LoadingGameActivity.this.runOnUiThread(() -> {
                tvLoadingProgress.setText(getResources().getString(R.string.connecting_to_host_play_msg));
                pbLoadingProgress.setProgress(isDone ? 5 : 6);
            });
        }

        @Override
        public void onGetHostApps(boolean isDone) {
            LoadingGameActivity.this.runOnUiThread(() -> {
                tvLoadingProgress.setText(getResources().getString(R.string.getting_necessary_data_play_msg));
                pbLoadingProgress.setProgress(isDone ? 7 : 8);
            });
        }

        @Override
        public void onPrepareHost(boolean isDone) {
            LoadingGameActivity.this.runOnUiThread(() -> {
                tvLoadingProgress.setText(getResources().getString(R.string.preparing_environment_play_msg));
                pbLoadingProgress.setProgress(isDone ? 9 : 10);
            });
        }

        @Override
        public void onStartConnect(boolean isDone) {
            LoadingGameActivity.this.runOnUiThread(() -> {
                tvLoadingProgress.setText(getResources().getString(R.string.done_play_msg));
                pbLoadingProgress.setProgress(isDone ? 11 : 12);
            });
        }

        @Override
        public void onStopConnect(boolean isDone, String err) {
            LoadingGameActivity.this.runOnUiThread(() -> {
                pbLoadingProgress.setProgress(1);
                LoadingGameActivity.this.finish();
            });
        }

        @Override
        public void onError(String err) {
            if (!err.contains("login"))
                LoadingGameActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show());
            LoadingGameActivity.this.finish();
        }
    };

}