package com.rora.phase.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.rora.phase.R;
import com.rora.phase.ui.home.game.adapter.BannerVPAdapter;
import com.rora.phase.ui.home.game.adapter.GameRecyclerViewAdapter;
import com.rora.phase.ui.home.viewmodel.HomeViewModel;
import com.rora.phase.utils.ui.CustomViewPagerTransformer;
import com.rora.phase.utils.ui.HorizontalMarginItemDecoration;

import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView rclvRecentPlay, rclvEditorsChoices, rclvHotGame;
    private SwipeRefreshLayout refreshLayout;
    private ViewPager2 vpGameBanner;
    private LinearLayout llRecentPlaySection;
    private ImageButton viewAllEditorsChoiceImb, viewAllHotGameImb;
    private ImageView errEditorsChoiceImv, errHotGameImv;

    private HomeViewModel homeViewModel;
    private BannerVPAdapter bannerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        refreshLayout = root.findViewById(R.id.refresh_layout_home_screen);
        vpGameBanner = root.findViewById(R.id.new_game_vp);
        rclvRecentPlay = root.findViewById(R.id.recent_play_rclv_home_screen);
        rclvEditorsChoices = root.findViewById(R.id.editors_choice_rclv_home_screen);
        rclvHotGame = root.findViewById(R.id.hot_game_rclv_home_screen);
        llRecentPlaySection = root.findViewById(R.id.recent_play_section_ll);
        errEditorsChoiceImv = root.findViewById(R.id.error_data_editor_choice_imv);
        errHotGameImv = root.findViewById(R.id.error_data_hot_game_imv);
        viewAllEditorsChoiceImb = root.findViewById(R.id.btn_view_all_editor_choice);
        viewAllHotGameImb = root.findViewById(R.id.btn_view_all_hot_game);

        root.findViewById(R.id.btn_view_all_recent_play).setOnClickListener(this);

        initView();
        bindData();
        return root;
    }

    private void initView() {
        bannerAdapter = new BannerVPAdapter();
        vpGameBanner.setAdapter(bannerAdapter);
        vpGameBanner.setOffscreenPageLimit(1);
        vpGameBanner.setPageTransformer(new CustomViewPagerTransformer());
        vpGameBanner.addItemDecoration(new HorizontalMarginItemDecoration());

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

        viewAllEditorsChoiceImb.setOnClickListener(this);
        viewAllHotGameImb.setOnClickListener(this);
    }

    private void bindData() {

        homeViewModel.getNewGameList().observe(getViewLifecycleOwner(), games -> bannerAdapter.bindData(games));

        homeViewModel.getRecentPlayList().observe(getViewLifecycleOwner(), games -> {
                llRecentPlaySection.setVisibility(games == null || games.size() == 0 ? View.GONE : View.VISIBLE);
                ((GameRecyclerViewAdapter) Objects.requireNonNull(rclvRecentPlay.getAdapter())).bindData(games);
            }
        );

        homeViewModel.getEditorsChoiceList().observe(getViewLifecycleOwner(), games -> {
            viewAllEditorsChoiceImb.setVisibility(games == null || games.size() == 0 ? View.GONE : View.VISIBLE);
            errEditorsChoiceImv.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameRecyclerViewAdapter) Objects.requireNonNull(rclvEditorsChoices.getAdapter())).bindData(games);
        });

        homeViewModel.getHotGameList().observe(getViewLifecycleOwner(), games -> {
            viewAllHotGameImb.setVisibility(games == null || games.size() == 0 ? View.GONE : View.VISIBLE);
            errHotGameImv.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameRecyclerViewAdapter) Objects.requireNonNull(rclvHotGame.getAdapter())).bindData(games);
        });

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_view_all_recent_play:
                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_home_to_gameListFragment);
                break;
            case R.id.btn_view_all_editor_choice:
                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_home_to_gameListFragment);
                break;
            case R.id.btn_view_all_hot_game:
                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_home_to_gameListFragment);
                break;
            default: break;
        }
    }
}