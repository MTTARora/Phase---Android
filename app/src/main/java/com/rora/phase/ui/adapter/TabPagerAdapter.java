package com.rora.phase.ui.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rora.phase.model.Game;
import com.rora.phase.ui.home.GameListFragment;

import java.util.List;

public class TabPagerAdapter extends FragmentStateAdapter {

    public TabPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new GameListFragment();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
