package com.rora.phase.ui.library;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rora.phase.R;
import com.rora.phase.ui.adapter.TabPagerAdapter;
import com.rora.phase.ui.auth.AuthActivity;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.ErrorView;

public class LibraryFragment extends BaseFragment {

    private TabLayout libraryTabLayout;
    private ViewPager2 libraryVp;
    private ImageView backgroundLl;
    private ErrorView frameError;
    private boolean needReload = true;

    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        View root = inflater.inflate(R.layout.fragment_library, container, false);
        backgroundLl = root.findViewById(R.id.bg_recent_play);
        libraryTabLayout = root.findViewById(R.id.library_tab_layout);
        libraryVp = root.findViewById(R.id.library_vp);
        frameError = root.findViewById(R.id.error_view);

        initData();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needReload) {
            setupViews();
            needReload = false;
        }
    }

    private void initData() {
        userViewModel.getCurrentRecentPlay().observe(getViewLifecycleOwner(), game -> {
            MediaHelper.loadImageWithBlurEffect(getContext(), backgroundLl, game == null ? null : game.getTile());
            hideLoadingScreen();
        });

        userViewModel.triggerLoginListener().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean requireLogin) {
                if (requireLogin) {
                    setupGuestViews();
                }
            }
        });
    }

    private void setupViews() {
        ViewCompat.setOnApplyWindowInsetsListener(libraryTabLayout, (v, insets) -> {
            // Move toolbar below status bar
            libraryTabLayout.setPadding(0, insets.getSystemWindowInsetTop() + (int)getResources().getDimension(R.dimen.minnn_space), 0, 0);
            return insets;
        });

        showLoadingScreen();
        if (!userViewModel.isUserLogged()) {
            setupGuestViews();
            return;
        }
        frameError.setVisibility(View.GONE);
        libraryVp.setVisibility(View.VISIBLE);

        TabPagerAdapter adapter = new TabPagerAdapter(this);
        adapter.addPage(getResources().getString(R.string.recent_play_title), new RecentPlayFragment());
        adapter.addPage(getResources().getString(R.string.favorite_title), new FavoriteFragment());

        libraryVp.setUserInputEnabled(false);
        libraryVp.setAdapter(adapter);
        new TabLayoutMediator(libraryTabLayout, libraryVp, ((tab, position) -> tab.setText(adapter.getTitle(position)))).attach();
    }

    private void setupGuestViews() {
        userViewModel.setCurrentRecentPlay(null);
        libraryVp.setVisibility(View.GONE);
        frameError.setVisibility(View.VISIBLE);
        frameError.setMsg(getResources().getString(R.string.require_login_msg));
        frameError.setAction(getResources().getString(R.string.login_title), v -> {
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.putExtra(AuthActivity.START_IN_APP_PARAM, true);
            startActivity(intent);
            needReload = true;
        });
    }

}