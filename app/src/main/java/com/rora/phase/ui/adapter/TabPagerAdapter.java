package com.rora.phase.ui.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rora.phase.model.Game;
import com.rora.phase.ui.home.GameListFragment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabPagerAdapter extends FragmentStateAdapter {

    private HashMap<String, String>[] params;

    public TabPagerAdapter(Fragment fragment, HashMap<String, String>... params) {
        super(fragment);
        this.params = params;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = GameListFragment.newInstance(params[position].get(GameListFragment.LIST_TYPE_PARAM), params[position].get(GameListFragment.KEY_FILTER_PARAM));

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
