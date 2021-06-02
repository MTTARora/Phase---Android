package com.rora.phase.ui.library;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.adapter.GameVerticalRVAdapter;
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.ListWithNotifyView;

public class FavoriteFragment extends BaseFragment {

    private ListWithNotifyView favoriteLv;

    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        favoriteLv = root.findViewById(R.id.favorite_list);

        setupViews();
        initData();
        return root;
    }

    private void setupViews() {
        favoriteLv.startLoading();
        favoriteLv.setupList(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false), new GameVerticalRVAdapter(favoriteLv.getListView()));

        ((GameVerticalRVAdapter) favoriteLv.getAdapter(false, null)).setOnItemSelectedListener((OnItemSelectedListener<Game>) (position, selectedItem) -> moveTo(GameDetailFragment.newInstance(selectedItem), GameDetailFragment.class.getSimpleName(), true));
    }

    private void initData() {
        userViewModel.getFavoriteList().observe(getViewLifecycleOwner(), games -> ((GameVerticalRVAdapter) favoriteLv.getAdapter(true, games)).bindData(games));
        userViewModel.getUpdateFavoriteResult().observe(getViewLifecycleOwner(), gameDataResponse -> {
            if (gameDataResponse.getData() != null)
                userViewModel.getFavoriteListData();
        });

        userViewModel.getFavoriteListData();
    }

}