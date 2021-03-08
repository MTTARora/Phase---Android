package com.rora.phase.utils.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.ui.game.GameListFragment;

import javax.annotation.Nullable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class BaseFragment extends Fragment {

    private ConstraintLayout customToolBar;
    private ImageButton backBtn;
    private TextView titleTv;
    public boolean stopUpDateHomeScreen = false;

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

        customToolBar = view.findViewById(R.id.custom_toolbar);
        backBtn = view.findViewById(R.id.back_btn);
        titleTv = view.findViewById(R.id.title_custom_toolbar);

        backBtn.setOnClickListener(v -> onBackPressed());
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
        setNavsVisibility(previousFrag);
        fm.popBackStack();
    }

    public void moveTo(Fragment newFragment, @Nullable String backStackName) {
        showLoadingScreen();
        setActionbarVisibility(newFragment);

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

    public void showLoadingScreen() {
        ((FrameLayout)getActivity().findViewById(R.id.main_loading_view)).setVisibility(VISIBLE);
    }

    public void hideLoadingScreen() {
        ((FrameLayout)getActivity().findViewById(R.id.main_loading_view)).setVisibility(View.INVISIBLE);
    }

    public void setScreenTitle(View root, String title) {
        ((TextView)root.findViewById(R.id.title_custom_toolbar)).setText(title);
    }

    private void setNavsVisibility(Fragment newFragment) {
        setBottomNavVisibility(newFragment);
        setActionbarVisibility(newFragment);
    }

    //------------------------------------------------------

    private void setActionbarVisibility(Fragment newFragment) {
        if (!((AppCompatActivity)getActivity()).getSupportActionBar().isShowing())
            return;

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void setBottomNavVisibility(Fragment newFragment) {
        if (newFragment instanceof GameDetailFragment
                || newFragment instanceof GameListFragment) {
            if (MainActivity.navView.getVisibility() != GONE) {
                MainActivity.navView.setVisibility(View.INVISIBLE);
                MainActivity.navView.setVisibility(GONE);
            }
        }
        else {
            if (MainActivity.navView.getVisibility() != VISIBLE)
                MainActivity.navView.setVisibility(VISIBLE);
        }
    }

}
