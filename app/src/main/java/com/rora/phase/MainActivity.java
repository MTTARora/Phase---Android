package com.rora.phase;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rora.phase.model.Game;
import com.rora.phase.model.UserPlayingData;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.ui.game.QueueView;
import com.rora.phase.ui.settings.auth.SignInActivity;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.callback.PlayGameProgressCallBack;
import com.rora.phase.utils.services.PlayServices;
import com.rora.phase.utils.ui.FragmentManagerHelper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    public static BottomNavigationView bottomNavView;
    private QueueView queueViewMain, queueView;

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
        queueViewMain = new QueueView(this);
        queueView = new QueueView(this);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavView, navController);

        //navController.addOnDestinationChangedListener(this);

        bottomMenu = (new PopupMenu(this, null)).getMenu();
        getMenuInflater().inflate(R.menu.bottom_nav_menu, bottomMenu);
        currentBottomTab = bottomMenu.getItem(0);
        //bottomNavView.setOnNavigationItemSelectedListener(this);

        Intent serviceIntent = new Intent(this, PlayServices.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);

        queueViewMain.setOnClickListener(v -> {
            FragmentManagerHelper.replace(getSupportFragmentManager(), R.id.main_container, GameDetailFragment.newInstance(managerBinder.getCurrentGame()), null);
        });
        queueView.setOnClickListener(v -> FragmentManagerHelper.replace(getSupportFragmentManager(), R.id.main_container, GameDetailFragment.newInstance(managerBinder.getCurrentGame()), null));
        findViewById(R.id.end_queue_main_btn).setOnClickListener(v -> managerBinder.stopConnect(null));
        findViewById(R.id.end_queue_btn).setOnClickListener(v -> managerBinder.stopConnect(null));
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
                || destination.getId() == R.id.navigation_dashboard
                || destination.getId() == R.id.navigation_notifications
                || destination.getId() == R.id.gameDetailFragment) {
            getSupportActionBar().hide();
        }
        else {
            getSupportActionBar().show();
        }
    }

    private void setBottomNavVisibility(NavDestination destination) {
        if (destination.getId() == R.id.navigation_home
                || destination.getId() == R.id.navigation_dashboard
                || destination.getId() == R.id.navigation_notifications) {
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

    private void binQueueData(int total, int currentPos) {
        if (managerBinder == null)
            return;

        gameViewModel.setCurrentGame(managerBinder.getCurrentGame());
        queueView.bindData(total, currentPos, managerBinder.getCurrentGame());
    }

    public void updateQueue(int visibilityMainFrame, int visibility) {
        if (managerBinder == null || managerBinder.getCurrentState() != UserPlayingData.PlayingState.IN_QUEUE)
            return;

        this.runOnUiThread(() -> {

            queueViewMain.setVisibility(visibilityMainFrame);

            int count = getSupportFragmentManager().getBackStackEntryCount();

            //if (count != 0 && visibility == VISIBLE) {
            //} else
            //    queueView.setVisibility(GONE);
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
        public void onJoinQueue(int total, int position) {
            MainActivity.this.runOnUiThread(() -> {
                binQueueData(total, position);
                updateQueue(VISIBLE, VISIBLE);
            });
        }

        @Override
        public void onAddPc(boolean isDone) {

        }

        @Override
        public void onPairPc(boolean isDone) {

        }

        @Override
        public void onStartConnect(boolean isDone) {

        }

        @Override
        public void onComputerUpdated(ComputerDetails computer) {

        }

        @Override
        public void onStopConnect(boolean isDone) {
            updateQueue(GONE, GONE);
            binQueueData(0, 0);
        }

        @Override
        public void onError(String err) {
            if (err.contains("login")) {
                MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show());
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                intent.putExtra(SignInActivity.START_IN_APP_PARAM, true);
                startActivity(intent);
            }
        }
    };
}