package com.rora.phase;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.rora.phase.ui.game.GameSettingsDialog;
import com.rora.phase.ui.settings.auth.AuthActivity;
import com.rora.phase.ui.viewmodel.GameViewModel;
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

    private GameViewModel gameViewModel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        findViewById(R.id.end_queue_main_btn).setOnClickListener(v -> managerBinder.stopConnect(null));
        findViewById(R.id.end_queue_btn).setOnClickListener(v -> managerBinder.stopConnect(null));
    }

    private void goToGameDetails() {
        Fragment currentDisplayScreen = FragmentManagerHelper.getCurrentFrag(getSupportFragmentManager(), R.id.main_container);
        if (currentDisplayScreen != null && currentDisplayScreen instanceof GameDetailFragment) {
            if(((GameDetailFragment) currentDisplayScreen).getCurrentGameId() != managerBinder.getCurrentGame().getId()) {
                findViewById(R.id.main_loading_view).setVisibility(VISIBLE);
                getSupportFragmentManager().popBackStack();
                FragmentManagerHelper.replace(getSupportFragmentManager(), R.id.main_container, GameDetailFragment.newInstance(managerBinder.getCurrentGame()), GameDetailFragment.class.getSimpleName());
            }

            return;
        }

        findViewById(R.id.main_loading_view).setVisibility(VISIBLE);
        FragmentManagerHelper.replace(getSupportFragmentManager(), R.id.main_container, GameDetailFragment.newInstance(managerBinder.getCurrentGame()), GameDetailFragment.class.getSimpleName());
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        queueViewMain.setVisibility(managerBinder.getCurrentState() == UserPlayingData.PlayingState.IN_QUEUE ? VISIBLE : GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(managerBinder != null)
            unbindService(serviceConnection);
    }

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

    //@Override
    //public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    //    if (currentBottomTab.getItemId() == item.getItemId()) {
    //        return false;
    //    }
    //
    //    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    //    Fragment newFragmentTab;
    //    switch (item.getItemId()) {
    //        case R.id.navigation_home:
    //            newFragmentTab = HomeFragment.newInstance();
    //            break;
    //        case R.id.navigation_dashboard:
    //            newFragmentTab = DashboardFragment.newInstance();
    //            break;
    //        default:
    //            newFragmentTab = SettingsFragment.newInstance();
    //            break;
    //    }
    //
    //    transaction.replace(R.id.main_container, newFragmentTab);
    //
    //    getSupportFragmentManager().popBackStack();
    //    transaction.commit();
    //
    //    currentBottomTab = item;
    //    return true;
    //}


    //---------------------------------- PLAY QUEUE ----------------------------------

    private void binQueueData(boolean isFirstInit, int currentPos) {
        if (managerBinder == null || managerBinder.getCurrentGame() == null)
            return;

        Game game = managerBinder.getCurrentGame();
        gameViewModel.setCurrentGame(managerBinder.getCurrentGame());
        this.runOnUiThread(() -> {
            tvQueueMain.setText(getResources().getString(R.string.in_queue_msg) + " " + currentPos);
            tvQueue.setText(getResources().getString(R.string.in_queue_msg) + " " + currentPos);

            if (currentPos == 0) {
                tvQueueMain.setText("--");
                tvQueue.setText("--");
                MediaHelper.loadImage(imvQueueMain, null);
                MediaHelper.loadImage(imvQueue, null);
                tvGameNameMain.setText("");
                tvGameName.setText("");
            }

            if (isFirstInit) {
                MediaHelper.loadImage(imvQueueMain, game.getBanner());
                MediaHelper.loadImage(imvQueue, game.getBanner());
                tvGameNameMain.setText(game.getName());
                tvGameName.setText(game.getName());
            }
        });
    }

    public void updateQueueVisibility(int visibilityMainFrame, int visibility) {
        if (managerBinder == null || (managerBinder.getCurrentState() != UserPlayingData.PlayingState.IN_QUEUE && managerBinder.getCurrentState() != UserPlayingData.PlayingState.STOPPED))
            return;

        this.runOnUiThread(() -> {

            queueViewMain.setVisibility(visibilityMainFrame);

            int count = getSupportFragmentManager().getBackStackEntryCount();

            queueView.setVisibility((count != 0 && visibility == VISIBLE) ? visibility : GONE);

        });
    }

    //--------------------------------------------------------------------------------



    private PlayGameProgressCallBack playGameProgressCallBack = new PlayGameProgressCallBack() {
        @Override
        public void onStart(boolean isDone) {

        }

        @Override
        public void onFindAHost(boolean isDone) {

        }

        @Override
        public void onQueueUpdated(boolean isFirstInit, int total, int position) {
            MainActivity.this.runOnUiThread(() -> {
                binQueueData(isFirstInit, position);
                updateQueueVisibility(VISIBLE, VISIBLE);
            });
        }

        @Override
        public void onPairPc(boolean isDone) {
            if (managerBinder.getCurrentState() != UserPlayingData.PlayingState.IN_QUEUE)
                return;

            MainActivity.this.runOnUiThread(() -> {
                tvQueueMain.setText(getResources().getString(R.string.connecting_to_host_play_msg));
                tvQueue.setText(getResources().getString(R.string.connecting_to_host_play_msg));
                queueMainPb.setVisibility(VISIBLE);
                queuePb.setVisibility(VISIBLE);
                queueMainPb.setMax(8);
                queuePb.setMax(8);
                queuePb.setProgress(isDone ? 1 : 2);
                queueMainPb.setProgress(isDone ? 1 : 2);
                queuePb.setProgress(isDone ? 1 : 2);
            });
        }

        @Override
        public void onGetHostApps(boolean isDone) {
            if (managerBinder.getCurrentState() != UserPlayingData.PlayingState.IN_QUEUE)
                return;

            MainActivity.this.runOnUiThread(() -> {
                tvQueueMain.setText(getResources().getString(R.string.getting_necessary_data_play_msg));
                tvQueue.setText(getResources().getString(R.string.getting_necessary_data_play_msg));
                queueMainPb.setProgress(isDone ? 3 : 4);
                queuePb.setProgress(isDone ? 3 : 4);
            });
        }

        @Override
        public void onPrepareHost(boolean isDone) {
            if (managerBinder.getCurrentState() != UserPlayingData.PlayingState.IN_QUEUE)
                return;

            MainActivity.this.runOnUiThread(() -> {
                tvQueueMain.setText(getResources().getString(R.string.preparing_environment_play_msg));
                tvQueue.setText(getResources().getString(R.string.preparing_environment_play_msg));
                queueMainPb.setProgress(isDone ? 5 : 6);
                queuePb.setProgress(isDone ? 5 : 6);
            });
        }

        @Override
        public void onStartConnect(boolean isDone) {
            if (managerBinder.getCurrentState() != UserPlayingData.PlayingState.IN_QUEUE)
                return;

            MainActivity.this.runOnUiThread(() -> {
                tvQueueMain.setText(getResources().getString(R.string.done_play_msg));
                tvQueue.setText(getResources().getString(R.string.done_play_msg));
                queueMainPb.setProgress(isDone ? 7 : 8);
                queuePb.setProgress(isDone ? 7 : 8);
                queueMainPb.setVisibility(GONE);
                queuePb.setVisibility(GONE);
                updateQueueVisibility(GONE, GONE);
                binQueueData(false, 0);
            });
        }

        @Override
        public void onStopConnect(boolean isDone, String err) {
            if (!isDone)
                return;

            if (err != null)
                MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show());

            queueMainPb.setVisibility(GONE);
            queuePb.setVisibility(GONE);
            updateQueueVisibility(GONE, GONE);
            binQueueData(false, 0);
            gameViewModel.setCurrentGame(null);
        }

        @Override
        public void onError(String err) {
            if (err.contains("login")) {
                MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show());
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                intent.putExtra(AuthActivity.START_IN_APP_PARAM, true);
                startActivity(intent);
            }
        }
    };

    @Override
    public void sendMessage(PlayServicesMessageSender.MsgCode code) {
        switch (code) {
            case PLAY:
                break;
            case STOP:
                managerBinder.stopConnect(playGameProgressCallBack);
                break;
            default:
                break;
        }
    }
}