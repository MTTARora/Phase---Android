package com.rora.phase.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.rora.phase.Game;
import com.rora.phase.R;
import com.rora.phase.ui.home.game.adapter.NewGameAdapter;

import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 newGameVP;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        newGameVP = root.findViewById(R.id.new_game_vp);

        initView();
        bindData();
        return root;
    }

    private void initView() {
        newGameVP.setAdapter(new NewGameAdapter(this));
    }

    private void bindData() {

        homeViewModel.getNewGame().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(@Nullable List<Game> games) {

            }
        });
    }
}