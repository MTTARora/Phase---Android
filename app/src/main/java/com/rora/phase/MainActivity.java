package com.rora.phase;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rora.phase.model.Game;
import com.rora.phase.model.UserPlayingData;
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.ui.auth.AuthActivity;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.Dialog;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.callback.PlayGameProgressCallBack;
import com.rora.phase.utils.services.PlayServices;
import com.rora.phase.utils.services.PlayServicesMessageSender;
import com.rora.phase.utils.ui.FragmentManagerHelper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements PlayServicesMessageSender.Sender {

    public static BottomNavigationView bottomNavView;

    private ConstraintLayout queueViewMain;
    private ConstraintLayout queueView;
    private ImageView imvQueueMain, imvQueue;
    private TextView tvQueueMain, tvQueue, tvGameNameMain, tvGameName;
    private ProgressBar queueMainPb, queuePb;
    private ImageButton triggerProgressMainBtn, triggerProgressBtn;

    private GameViewModel gameViewModel;
    private UserViewModel userViewModel;
    private boolean alreadyMoveToLogin = false;
    private MenuItem currentBottomTab;
    private Menu bottomMenu;
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
                    managerBinder.setListener(playGameProgressCallBack);
                }
            }.start();
        }

        public void onServiceDisconnected(ComponentName className) {
            managerBinder = null;
        }
    };


    //-------------------------------- LIFECYCLE --------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        bottomNavView = findViewById(R.id.bottom_nav_view);

        queueViewMain = findViewById(R.id.frame_queue_main);
        queueView = findViewById(R.id.frame_queue);
        imvQueueMain = findViewById(R.id.game_banner_queue_main_imv);
        imvQueue = findViewById(R.id.game_banner_queue_imv);
        tvQueueMain = findViewById(R.id.queue_main_tv);
        tvQueue = findViewById(R.id.queue_tv);
        tvGameNameMain = findViewById(R.id.tv_game_name_queue_main);
        tvGameName = findViewById(R.id.tv_game_name_queue);
        queueMainPb = findViewById(R.id.loading_game_queue_main_pb);
        queuePb = findViewById(R.id.loading_game_queue_pb);
        triggerProgressMainBtn = findViewById(R.id.end_queue_main_btn);
        triggerProgressBtn = findViewById(R.id.end_queue_btn);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavView, navController);

        bottomMenu = (new PopupMenu(this, null)).getMenu();
        getMenuInflater().inflate(R.menu.bottom_nav_menu, bottomMenu);
        currentBottomTab = bottomMenu.getItem(0);

        Intent serviceIntent = new Intent(this, PlayServices.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);

        queueViewMain.setOnClickListener(v -> goToGameDetails());
        queueView.setOnClickListener(v -> goToGameDetails());
        triggerProgressMainBtn.setOnClickListener(v -> quitSession());
        triggerProgressBtn.setOnClickListener(v -> quitSession());
        //setupSettingDialog();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setupData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        alreadyMoveToLogin = false;
        queueViewMain.setVisibility(managerBinder.getCurrentState() == UserPlayingData.PlayingState.IN_QUEUE ? VISIBLE : GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(managerBinder != null)
            unbindService(serviceConnection);
    }

    //-------------------------------- END LIFECYCLE --------------------------------


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    //@Override
    //public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
    //    setBottomNavVisibility(destination);
    //    setActionbarVisibility(destination);
    //}

    private void setupData() {
        userViewModel.triggerLoginListener().observe(this, requireLogin -> {
            if (requireLogin && alreadyMoveToLogin)
                goToLoginScreen();
        });
    }

    private void setActionbarVisibility(NavDestination destination) {
        if (destination.getId() == R.id.navigation_home
                || destination.getId() == R.id.navigation_search
                || destination.getId() == R.id.navigation_settings) {
            getSupportActionBar().hide();
        }
        else {
            getSupportActionBar().show();
        }
    }

    private void setBottomNavVisibility(NavDestination destination) {
        if (destination.getId() == R.id.navigation_home
                || destination.getId() == R.id.navigation_search
                || destination.getId() == R.id.navigation_settings) {
            setBottomNavigationVisibility(VISIBLE);
        }
        else {
            setBottomNavigationVisibility(GONE);
        }
    }

    public static void setBottomNavigationVisibility(int visibility) {
        if (bottomNavView != null)
            bottomNavView.setVisibility(visibility);
    }

    private void quitSession() {
        if (managerBinder == null || managerBinder.getCurrentPlayingGame() == null)
            return;

        if(managerBinder.getCurrentState() == UserPlayingData.PlayingState.PAUSED) {
            Dialog.displayDialog(this, getResources().getString(R.string.quit_queue_confirmation_text), null, getResources().getString(R.string.continue_text), getResources().getString(R.string.stop_text), () -> {
                managerBinder.resumeSession(MainActivity.this);
            }, () -> {
                managerBinder.stopConnect(null);
            });
        } else {
            Dialog.displayDialog(this, getResources().getString(R.string.quit_queue_confirmation_text), null, null, null, () -> {
                managerBinder.stopConnect(null);
            }, null);
        }
    }

    private void goToGameDetails() {
        Fragment currentDisplayScreen = FragmentManagerHelper.getCurrentFrag(getSupportFragmentManager(), R.id.main_container);
        if (currentDisplayScreen != null && currentDisplayScreen instanceof GameDetailFragment) {
            if(((GameDetailFragment) currentDisplayScreen).getCurrentGameId() != managerBinder.getCurrentPlayingGame().getId()) {
                findViewById(R.id.main_loading_view).setVisibility(VISIBLE);
                getSupportFragmentManager().popBackStack();
                FragmentManagerHelper.replace(getSupportFragmentManager(), R.id.main_container, GameDetailFragment.newInstance(managerBinder.getCurrentPlayingGame()), GameDetailFragment.class.getSimpleName());
            }

            return;
        }

        findViewById(R.id.main_loading_view).setVisibility(VISIBLE);
        FragmentManagerHelper.replace(getSupportFragmentManager(), R.id.main_container, GameDetailFragment.newInstance(managerBinder.getCurrentPlayingGame()), GameDetailFragment.class.getSimpleName());
    }

    private void goToLoginScreen() {
        alreadyMoveToLogin = true;
        runOnUiThread(() -> Toast.makeText(this, getString(R.string.require_login_msg), Toast.LENGTH_LONG).show());
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.putExtra(AuthActivity.START_IN_APP_PARAM, true);
        startActivity(intent);
    }

    //---------------------------------- PLAY QUEUE ----------------------------------

    private void initQueueData(int position) {
        if (managerBinder == null || managerBinder.getCurrentPlayingGame() == null)
            return;

        Game game = managerBinder.getCurrentPlayingGame();
        gameViewModel.setCurrentGame(managerBinder.getCurrentPlayingGame());
        MainActivity.this.runOnUiThread(() -> {
            MediaHelper.loadImage(imvQueueMain, game.getBanner().get_640x360());
            MediaHelper.loadImage(imvQueue, game.getBanner().get_640x360());
            tvGameNameMain.setText(game.getName());
            tvGameName.setText(game.getName());
            updateQueuePosition(position);
            updateQueueVisibility(VISIBLE, VISIBLE);
            MediaHelper.loadSvg(triggerProgressMainBtn, android.R.drawable.presence_offline);
            MediaHelper.loadSvg(triggerProgressBtn, android.R.drawable.presence_offline);
        });
    }

    private void initGameSessionData() {
        if (managerBinder == null || managerBinder.getCurrentPlayingGame() == null)
            return;

        Game game = managerBinder.getCurrentPlayingGame();
        gameViewModel.setCurrentGame(managerBinder.getCurrentPlayingGame());
        MainActivity.this.runOnUiThread(() -> {
            MediaHelper.loadImage(imvQueueMain, game.getBanner().get_640x360());
            MediaHelper.loadImage(imvQueue, game.getBanner().get_640x360());
            tvGameNameMain.setText(game.getName());
            tvGameName.setText(game.getName());
            tvQueueMain.setText(getResources().getString(R.string.in_game_session_msg));
            tvQueue.setText(getResources().getString(R.string.in_game_session_msg));
            updateQueueVisibility(VISIBLE, VISIBLE);
            MediaHelper.loadSvg(triggerProgressMainBtn, R.drawable.ic_play);
            MediaHelper.loadSvg(triggerProgressBtn, R.drawable.ic_play);
            queueMainPb.setVisibility(GONE);
            queuePb.setVisibility(GONE);
        });
    }

    private void resetQueueData() {
        MainActivity.this.runOnUiThread(() -> {
            MediaHelper.loadImage(imvQueueMain, null);
            MediaHelper.loadImage(imvQueue, null);
            tvGameNameMain.setText("");
            tvGameName.setText("");
            updateQueuePosition(0);
            updateQueueVisibility(GONE, GONE);
        });
    }

    private void updateQueuePosition(int position) {
        if (managerBinder == null || managerBinder.getCurrentPlayingGame() == null)
            return;

        MainActivity.this.runOnUiThread(() -> {
            tvQueueMain.setText(position == 0 ? "--" : getResources().getString(R.string.in_queue_msg) + " " + position);
            tvQueue.setText(position == 0 ? "--" : getResources().getString(R.string.in_queue_msg) + " " + position);
        });
    }

    private void updateQueueProgress(String msg, int progress) {
        if (managerBinder.getCurrentState() != UserPlayingData.PlayingState.IN_QUEUE)
            return;

        MainActivity.this.runOnUiThread(() -> {
            tvQueueMain.setText(msg);
            tvQueue.setText(msg);
            if (progress == 1) {
                queueMainPb.setMax(8);
                queuePb.setMax(8);
                queueMainPb.setVisibility(VISIBLE);
                queuePb.setVisibility(VISIBLE);
            }
            queueMainPb.setProgress(progress);
            queuePb.setProgress(progress);
        });
    }

    public void updateQueueVisibility(int visibilityMainFrame, int visibility) {
        MainActivity.this.runOnUiThread(() -> {

            //if (visibility == GONE) {
            //    queueViewMain.setVisibility(GONE);
            //    queueView.setVisibility(GONE);
            //    return;
            //}

            if (managerBinder == null)
                return;

            if (managerBinder.getCurrentState() == UserPlayingData.PlayingState.IN_QUEUE || managerBinder.getCurrentState() == UserPlayingData.PlayingState.STOPPED || managerBinder.getCurrentState() == UserPlayingData.PlayingState.PAUSED) {
                queueViewMain.setVisibility(visibilityMainFrame);

                int count = getSupportFragmentManager().getBackStackEntryCount();

                queueView.setVisibility((count != 0 && visibility == VISIBLE) ? visibility : GONE);
            } else {
                queueViewMain.setVisibility(GONE);
                queueView.setVisibility(GONE);
            }

        });
    }

    //--------------------------------------------------------------------------------



    private PlayGameProgressCallBack playGameProgressCallBack = new PlayGameProgressCallBack() {
        @Override
        public void onStart(boolean isDone) { }

        @Override
        public void onConnectionEstablished(boolean isSuccess) { }

        @Override
        public void onFindAHost(boolean isDone) { }

        @Override
        public void onQueueUpdated(boolean isFirstInit, int total, int position) {
            if (isFirstInit) {
                initQueueData(position);
            } else {
                updateQueuePosition(position);
            }
        }

        @Override
        public void onPairPc(boolean isDone) {
            updateQueueProgress(getResources().getString(R.string.connecting_to_host_play_msg), isDone ? 1 : 2);
        }

        @Override
        public void onGetHostApps(boolean isDone) {
            updateQueueProgress(getResources().getString(R.string.getting_necessary_data_play_msg), isDone ? 3 : 4);
        }

        @Override
        public void onPrepareHost(boolean isDone) {
            updateQueueProgress(getResources().getString(R.string.preparing_environment_play_msg), isDone ? 5 : 6);
        }

        @Override
        public void onStartConnect(boolean isDone) {
            updateQueueProgress(getResources().getString(R.string.done_connection_msg), isDone ? 7 : 8);
            resetQueueData();
        }

        @Override
        public void onPaused(String err) {
            if (err != null) {
                MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show());
                return;
            }
            initGameSessionData();
        }

        @Override
        public void onResumed(String err) {
            if (err != null) {
                MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show());
                return;
            }

            resetQueueData();
        }

        @Override
        public void onStopConnect(boolean isDone, String err) {
            if (!isDone)
                return;

            if (err != null) {
                Dialog.displayNotifyDialog(MainActivity.this, getString(R.string.err), err, null, null);
            }
                //MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show());

            resetQueueData();
            gameViewModel.setCurrentGame(null);
        }

        @Override
        public void onError(String err) {
            if (err.contains("login"))
                goToLoginScreen();
        }
    };

    @Override
    public void sendMessage(PlayServicesMessageSender.MsgCode code) {
        switch (code) {
            case PLAY:
                break;
            case SWITCH:
                managerBinder.stopConnect(null);
                break;
            case RESUME:
            case STOP:
                quitSession();
                break;
            default:
                break;
        }
    }
}