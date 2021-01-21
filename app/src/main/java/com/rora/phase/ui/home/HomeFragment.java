package com.rora.phase.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.home.game.adapter.BannerVPAdapter;
import com.rora.phase.ui.home.game.adapter.GameRecyclerViewAdapter;
import com.rora.phase.utils.ui.CustomViewPagerTransformer;
import com.rora.phase.utils.ui.HorizontalMarginItemDecoration;

import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private RecyclerView rclvRecentPlay, rclvEditorsChoices, rclvHotGame;
    private SwipeRefreshLayout refreshLayout;
    private ViewPager2 gameBannerVP;

    private HomeViewModel homeViewModel;
    private BannerVPAdapter bannerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        refreshLayout = root.findViewById(R.id.refresh_layout_home_screen);
        gameBannerVP = root.findViewById(R.id.new_game_vp);
        rclvRecentPlay = root.findViewById(R.id.recent_play_rclv_home_screen);
        rclvEditorsChoices = root.findViewById(R.id.editors_choice_rclv_home_screen);
        rclvHotGame = root.findViewById(R.id.hot_game_rclv_home_screen);

        initView();
        bindData();
        return root;
    }

    private void initView() {
        bannerAdapter = new BannerVPAdapter();
        gameBannerVP.setAdapter(bannerAdapter);
        gameBannerVP.setOffscreenPageLimit(1);
        gameBannerVP.setPageTransformer(new CustomViewPagerTransformer());
        gameBannerVP.addItemDecoration(new HorizontalMarginItemDecoration());

        refreshLayout.setOnRefreshListener(() -> {
            bindData();
            refreshLayout.setRefreshing(false);
        });

        rclvRecentPlay.setAdapter(new GameRecyclerViewAdapter());
        rclvRecentPlay.setLayoutManager(setUpLayoutManager(2));
        rclvRecentPlay.setHasFixedSize(true);

        rclvRecentPlay.setAdapter(new GameRecyclerViewAdapter());
        rclvRecentPlay.setLayoutManager(setUpLayoutManager(2));
        rclvRecentPlay.setHasFixedSize(true);

        rclvEditorsChoices.setAdapter(new GameRecyclerViewAdapter());
        rclvEditorsChoices.setLayoutManager(setUpLayoutManager(2));
        rclvEditorsChoices.setHasFixedSize(true);

        rclvHotGame.setAdapter(new GameRecyclerViewAdapter());
        rclvHotGame.setLayoutManager(setUpLayoutManager(2));
        rclvHotGame.setHasFixedSize(true);
    }

    private void bindData() {

        homeViewModel.getNewGameList().observe(getViewLifecycleOwner(), games -> bannerAdapter.bindData(games));

        homeViewModel.getRecentPlayList().observe(getViewLifecycleOwner(), games -> ((GameRecyclerViewAdapter) Objects.requireNonNull(rclvRecentPlay.getAdapter())).bindData(games));

        homeViewModel.getEditorsChoiceList().observe(getViewLifecycleOwner(), games -> ((GameRecyclerViewAdapter) Objects.requireNonNull(rclvEditorsChoices.getAdapter())).bindData(games));

        homeViewModel.getHotGameList().observe(getViewLifecycleOwner(), games -> ((GameRecyclerViewAdapter) Objects.requireNonNull(rclvHotGame.getAdapter())).bindData(games));

        homeViewModel.getNewGameListData();
        homeViewModel.getRecentPlayData();
        homeViewModel.getEditorsChoiceListData();
        homeViewModel.getHotGameListData();

    }

    private LinearLayoutManager setUpLayoutManager(int scale) {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.width = getWidth()/scale;
                return super.checkLayoutParams(lp);
            }
        };
    }
}