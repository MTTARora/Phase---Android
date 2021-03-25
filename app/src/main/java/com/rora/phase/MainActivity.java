package com.rora.phase;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rora.phase.ui.dashboard.DashboardFragment;
import com.rora.phase.ui.home.HomeFragment;
import com.rora.phase.ui.settings.SettingsFragment;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.utils.services.PlayServices;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    public static BottomNavigationView bottomNavView;

    private GameViewModel gameViewModel;
    private MenuItem currentBottomTab;
    private Menu bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        bottomNavView = findViewById(R.id.bottom_nav_view);
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

        startService(new Intent(this, PlayServices.class));
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

    public void setBottomNavigationVisibility(int visibility) {
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
}