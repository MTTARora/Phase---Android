package com.rora.phase.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rora.phase.R;
import com.rora.phase.ui.adapter.TabPagerAdapter;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseFragment;

public class LibraryFragment extends BaseFragment {

    private TabLayout libraryTabLayout;
    private ViewPager2 libraryVp;
    private ImageView backgroundLl;

    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        View root = inflater.inflate(R.layout.fragment_library, container, false);
        backgroundLl = root.findViewById(R.id.bg_recent_play);
        libraryTabLayout = root.findViewById(R.id.library_tab_layout);
        libraryVp = root.findViewById(R.id.library_vp);

        setupViews();
        initData();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initData() {
        userViewModel.getCurrentRecentPlay().observe(getViewLifecycleOwner(), game -> MediaHelper.loadImageWithBlurEffect(getContext(), backgroundLl, game.getBackground()));
    }

    private void setupViews() {
        TabPagerAdapter adapter = new TabPagerAdapter(this);
        adapter.addPage(getResources().getString(R.string.recent_play_title), new RecentPlayFragment());
        adapter.addPage(getResources().getString(R.string.favorite_title), new FavoriteFragment());

        libraryVp.setUserInputEnabled(false);
        libraryVp.setAdapter(adapter);
        new TabLayoutMediator(libraryTabLayout, libraryVp, ((tab, position) -> tab.setText(adapter.getTitle(position)))).attach();
    }

}