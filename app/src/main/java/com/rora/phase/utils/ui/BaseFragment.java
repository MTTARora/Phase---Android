package com.rora.phase.utils.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.ui.game.GameListFragment;
import com.rora.phase.ui.home.HomeFragment;
import com.rora.phase.utils.services.PlayServicesMessageSender;

import javax.annotation.Nullable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class BaseFragment extends Fragment {

    private CustomToolbar customActionBar;

    public boolean stopUpDateHomeScreen = false;
    private FragmentManager fm;
    private Fragment currentFragment;
    protected PlayServicesMessageSender.Sender playServicesMsgSenderCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            playServicesMsgSenderCallback = (PlayServicesMessageSender.Sender) context;
        } catch (ClassCastException e) {
            // Error, class doesn't implement the interface
        }
    }

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

        initData();
        setNavsVisibility(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        playServicesMsgSenderCallback = null;
    }

    private void onBackPressed() {
        Fragment previousFrag = null;

        if (fm.getBackStackEntryCount() > 1) {
            String tag = (fm.getBackStackEntryAt(fm.getBackStackEntryCount()-2)).getName();
            previousFrag = fm.findFragmentByTag(tag);
        }

        if (previousFrag == null) {
            stopUpDateHomeScreen = false;
            setNavsVisibility(null);
            ((MainActivity)getActivity()).updateQueueVisibility(VISIBLE, GONE);
        }

        if (currentFragment != null) {
            fm.popBackStack(fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            hideLoadingScreen();
        }
    }

    private void initData() {
        fm = getActivity().getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count == 0)
            return;
        String currentFragTag = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName();
        currentFragment = fm.findFragmentByTag(currentFragTag);
    }

    public void moveTo(Fragment newFragment, @Nullable String backStackName, boolean showLoading) {
        if (showLoading)
            showLoadingScreen();

        currentFragment = newFragment;
        FragmentManagerHelper.replace(fm, R.id.main_container, newFragment, backStackName);
        //FragmentTransaction transaction = fm.beginTransaction();
        //transaction.setCustomAnimations(
        //        R.anim.screen_fadein,  // enter
        //        R.anim.screen_fadeout,  // exit
        //        R.anim.screen_popup_show,   // popEnter
        //        R.anim.screen_popup_hide  // popExit
        //);
        //transaction.replace(R.id.main_container, newFragment, newFragment.getClass().getSimpleName());
        //transaction.addToBackStack(backStackName);
        //transaction.commit();
    }

    public void popupScreen(Fragment screen, @Nullable String stackName, boolean showLoading) {
        if (showLoading)
            showLoadingScreen();

        currentFragment = screen;
        FragmentManagerHelper.add(fm, R.id.main_container, screen, stackName);
    }


    //----------------------------- ACTIONBAR --------------------------------

    public void showActionbar(View root, String title, boolean enableBackBtn, View.OnClickListener onBackPressed) {
        customActionBar = root.findViewById(R.id.toolbar);
        customActionBar.showActionbar((AppCompatActivity) getActivity(), title, enableBackBtn, onBackPressed);
    }

    public void hideActionbar(View root) {
        customActionBar = root.findViewById(R.id.toolbar);
        customActionBar.hideActionbar();
    }

    public LinearLayout getActionBar() {
        return customActionBar;
    }

    public void setScreenTitle(View root, String title) {
        customActionBar = root.findViewById(R.id.toolbar);
        customActionBar.setScreenTitle(title);
    }

    public void setActionbarStyle(float size, int color) {
        customActionBar.setActionbarStyle(size, color);
    }

    //------------------------------------------------------------------------------------


    public void showLoadingScreen() {
        (getActivity().findViewById(R.id.main_loading_view)).setVisibility(VISIBLE);
    }

    public void hideLoadingScreen() {
        if ((getActivity().findViewById(R.id.main_loading_view)).getVisibility() == GONE)
            return;

        //try {
            //Thread.sleep(500);
            (getActivity().findViewById(R.id.main_loading_view)).setVisibility(GONE);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
    }

    private void setNavsVisibility(Fragment newFragment) {
        //setBottomNavVisibility(newFragment);
        //setActionbarVisibility(newFragment);
        setQueueFrameVisibility();
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
        if (fm.getBackStackEntryCount() == 0) {
            MainActivity.setBottomNavigationVisibility(VISIBLE);
            return;
        }

        if (newFragment instanceof GameDetailFragment
                || newFragment instanceof GameListFragment) {
            MainActivity.setBottomNavigationVisibility(GONE);
        }
        else {
            MainActivity.setBottomNavigationVisibility(VISIBLE);
        }
    }

    private void setQueueFrameVisibility() {
        if (currentFragment == null)
            ((MainActivity) getActivity()).updateQueueVisibility(VISIBLE, GONE);
        else
            ((MainActivity) getActivity()).updateQueueVisibility(GONE, VISIBLE);
    }

}
