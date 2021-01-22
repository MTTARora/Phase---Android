package com.rora.phase.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rora.phase.R;
import com.rora.phase.model.Game;

import java.util.List;

public class GameListFragment extends Fragment {

    private static final String ARG_LIST_NAME = "listName";
    private static final String ARG_PARAM2 = "param2";

    private String mListName;
    private List<Game> mGameList;

    public GameListFragment() {}

    public static GameListFragment newInstance(String listName, String param2) {
        GameListFragment fragment = new GameListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_NAME, listName);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mListName = getArguments().getString(ARG_LIST_NAME);
            mGameList = getArguments().getParcelableArrayList(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game_list, container, false);

        initView();

        return root;
    }

    private void initView() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mListName == null ? "Game" : mListName);
    }

}