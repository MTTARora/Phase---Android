package com.rora.phase.utils.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.ui.game.GameListFragment;
import com.rora.phase.ui.home.HomeFragment;

import javax.annotation.Nullable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class BaseFragment extends Fragment {

    public boolean stopUpDateHomeScreen = false;
    private Fragment currentFragment;

    @Override
    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                onBackPressed();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.back_btn).setOnClickListener(v -> onBackPressed());

        //setActionbarVisibility();
    }

    private void onBackPressed() {
        FragmentManager fm = getParentFragmentManager();

        Fragment previousFrag = null;
        if (fm.getBackStackEntryCount() > 1) {
            String tag = (fm.getBackStackEntryAt(fm.getBackStackEntryCount()-2)).getName();
            previousFrag = fm.findFragmentByTag(tag);
        }

        if (previousFrag == null)
            stopUpDateHomeScreen = false;
        //setNavsVisibility(previousFrag);
        fm.popBackStack();
    }

    public void moveTo(Fragment newFragment, @Nullable String backStackName) {
        showLoadingScreen();

        currentFragment = newFragment;
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.screen_fadein,  // enter
                R.anim.screen_fadeout,  // exit
                R.anim.screen_popup_show,   // popEnter
                R.anim.screen_popup_hide  // popExit
        );
        transaction.replace(R.id.main_container, newFragment, newFragment.getClass().getSimpleName());
        transaction.addToBackStack(backStackName);
        transaction.commit();
    }

    public void showActionbar(View root, String title, boolean enableBackBtn) {
        root.findViewById(R.id.custom_toolbar).setVisibility(VISIBLE);
        enableBackButton(root, enableBackBtn);
        setScreenTitle(root, title);
    }

    public void hideActionbar(View root) {
        root.findViewById(R.id.custom_toolbar).setVisibility(GONE);
        enableBackButton(root, false);
    }

    public void setScreenTitle(View root, String title) {
        ((TextView)root.findViewById(R.id.title_custom_toolbar)).setText(title);
    }

    public void setActionbarStyle(View root, float size, int color) {
        if (size != 0)
            ((TextView)root.findViewById(R.id.title_custom_toolbar)).setTextSize(size);
        if (color != 0)
            ((TextView)root.findViewById(R.id.title_custom_toolbar)).setTextColor(color);
    }

    private void enableBackButton(View root, boolean enable) {
        ImageButton backBtn = root.findViewById(R.id.back_btn);
        if (enable) {
            backBtn.setVisibility(VISIBLE);
            backBtn.setOnClickListener(v -> onBackPressed());
        } else {
            backBtn.setVisibility(GONE);
            ViewHelper.setMargins(root.findViewById(R.id.custom_toolbar), (int) getResources().getDimension(R.dimen.normal_space), 0, (int) getResources().getDimension(R.dimen.normal_space), 0);
        }
    }

    public void showLoadingScreen() {
        (getActivity().findViewById(R.id.main_loading_view)).setVisibility(VISIBLE);
    }

    public void hideLoadingScreen() {
        try {
            Thread.sleep(500);
            (getActivity().findViewById(R.id.main_loading_view)).setVisibility(GONE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------

    private void setNavsVisibility(Fragment newFragment) {
        setBottomNavVisibility(newFragment);
        setActionbarVisibility(newFragment);
    }

    private void setActionbarVisibility(Fragment newFragment) {
        if (newFragment instanceof HomeFragment
                || newFragment instanceof GameListFragment) {

        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void setBottomNavVisibility(Fragment newFragment) {
        if (newFragment instanceof GameDetailFragment
                || newFragment instanceof GameListFragment) {
            if (MainActivity.bottomNavView.getVisibility() != GONE) {
                MainActivity.bottomNavView.setVisibility(View.INVISIBLE);
                MainActivity.bottomNavView.setVisibility(GONE);
            }
        }
        else {
            if (MainActivity.bottomNavView.getVisibility() != VISIBLE)
                MainActivity.bottomNavView.setVisibility(VISIBLE);
        }
    }

}
