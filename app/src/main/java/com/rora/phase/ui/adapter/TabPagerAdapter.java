package com.rora.phase.ui.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rora.phase.model.Game;
import com.rora.phase.ui.home.GameListFragment;
import com.rora.phase.ui.viewmodel.HomeViewModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabPagerAdapter extends FragmentStateAdapter {

    private List<Bundle> params;

    public static final String TAB_TITLE = "title";

    public TabPagerAdapter(Fragment fragment, List<Bundle> params) {
        super(fragment);
        this.params = params;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = GameListFragment.newInstance((HomeViewModel.GameListType) params.get(position).getSerializable(GameListFragment.LIST_TYPE_PARAM), params.get(position).getString(GameListFragment.KEY_FILTER_PARAM));

        return fragment;
    }


    @Override
    public int getItemCount() {
        return params.size();
    }

}
