package com.rora.phase.ui.home.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.rora.phase.Game;
import com.rora.phase.R;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameBannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameBannerFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private Serializable mParam1;

    public GameBannerFragment() {
    }

    public static GameBannerFragment newInstance(Game gameParam) {
        GameBannerFragment fragment = new GameBannerFragment();
        Bundle args = new Bundle();
        //args.putSerializable(ARG_PARAM1, gameParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_banner, container, false);
    }
}