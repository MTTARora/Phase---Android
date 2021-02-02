package com.rora.phase.ui.game;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rora.phase.R;

public class GameDetailFragment extends Fragment {

    public GameDetailFragment() {
    }

    public static GameDetailFragment newInstance() {
        GameDetailFragment fragment = new GameDetailFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game_detail, container, false);

        (root.findViewById(R.id.btn_play)).setOnClickListener(v -> {

        });

        return root;
    }

}