package com.rora.phase.ui.home.game.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rora.phase.Game;
import com.rora.phase.ui.home.game.GameBannerFragment;

import java.util.ArrayList;
import java.util.List;

public class NewGameAdapter extends FragmentStateAdapter {

    private List<Game> newGameList;

    public NewGameAdapter(@NonNull androidx.fragment.app.Fragment fragment) {
        super(fragment);
        newGameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return GameBannerFragment.newInstance(newGameList.get(position));
    }


    @Override
    public int getItemCount() {
        return newGameList.size();
    }
}
