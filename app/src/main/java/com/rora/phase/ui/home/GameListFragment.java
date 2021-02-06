package com.rora.phase.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter;
import com.rora.phase.ui.adapter.GameVerticalRecycerViewAdapter;
import com.rora.phase.ui.viewmodel.HomeViewModel;

import java.util.List;
import java.util.Objects;

public class GameListFragment extends Fragment {

    private RecyclerView rclvGameList;

    private HomeViewModel homeViewModel;

    public GameListFragment() {}

    public static GameListFragment newInstance() {
        GameListFragment fragment = new GameListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_game_list, container, false);
        rclvGameList = root.findViewById(R.id.game_list_vertical_rclv);

        initView();
        bindData();

        return root;

    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL , false);
        rclvGameList.setAdapter(new GameVerticalRecycerViewAdapter());
        rclvGameList.setLayoutManager(linearLayoutManager);
    }

    private void bindData() {

        homeViewModel.getRecommendedGameList().observe(getViewLifecycleOwner(), games -> {
            ((GameVerticalRecycerViewAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
        });

        homeViewModel.getGamesByPayTypeList().observe(getViewLifecycleOwner(), games -> {
            ((GameVerticalRecycerViewAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
        });

        homeViewModel.getGamesByPayTypeList().observe(getViewLifecycleOwner(), games -> {
            ((GameVerticalRecycerViewAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
        });

        homeViewModel.getGamesByPayTypeList().observe(getViewLifecycleOwner(), games -> {
            ((GameVerticalRecycerViewAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
        });

    }

}