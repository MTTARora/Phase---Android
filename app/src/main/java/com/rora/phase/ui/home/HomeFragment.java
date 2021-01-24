package com.rora.phase.ui.home;

import android.os.Bundle;
import android.os.Handler;
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
import com.rora.phase.ui.home.game.adapter.GameInfoRecyclerViewAdapter;
import com.rora.phase.ui.home.game.adapter.GameMinInfoRecyclerViewAdapter;
import com.rora.phase.ui.home.viewmodel.HomeViewModel;
import com.rora.phase.utils.ui.CustomViewPagerTransformer;
import com.rora.phase.utils.ui.HorizontalMarginItemDecoration;

import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView rclvHotGame, rclvNewGame, rclvTrending, rclvEditorChoice;
    private SwipeRefreshLayout refreshLayout;
    private ViewPager2 vpBanner;
    private ImageButton viewAllNewImb, viewAllEditorChoiceImb, viewAllTrendingImb, viewAllHotGameImb;
    private ImageView errEditorChoiceImv, errTrendingImv, errHotGameImv;

    private HomeViewModel homeViewModel;
    private BannerVPAdapter bannerAdapter;
    private boolean isSlidingBanner = false;

    private Handler bannerRunnableHandler = new Handler();
    private Runnable bannerAutoSlideRunnable = new Runnable() {
        @Override
        public void run() {
            isSlidingBanner = true;
            int currentPos = vpBanner.getCurrentItem();
            vpBanner.setCurrentItem(currentPos == bannerAdapter.getItemCount()-1 ? 0 : currentPos+1);
            bannerRunnableHandler.postDelayed(this, 3000);
        }
    };


    //------------------------ LIFECYCLE --------------------------

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        refreshLayout = root.findViewById(R.id.refresh_layout_home_screen);
        vpBanner = root.findViewById(R.id.banner_vp);

        rclvNewGame = root.findViewById(R.id.new_game_rclv_home_screen);
        rclvEditorChoice = root.findViewById(R.id.editor_choice_rclv_home_screen);
        rclvTrending = root.findViewById(R.id.trending_rclv_home_screen);
        rclvHotGame = root.findViewById(R.id.hot_game_rclv_home_screen);

        errEditorChoiceImv = root.findViewById(R.id.error_data_editor_choice_imv);
        errTrendingImv = root.findViewById(R.id.error_data_trending_imv);
        errHotGameImv = root.findViewById(R.id.error_data_hot_game_imv);

        //viewAllNewImb = root.findViewById(R.id.btn_view_all_editor_choice);
        viewAllEditorChoiceImb = root.findViewById(R.id.btn_view_all_editor_choice);
        viewAllTrendingImb = root.findViewById(R.id.btn_view_all_trending);
        viewAllHotGameImb = root.findViewById(R.id.btn_view_all_hot_game);

        initView();
        bindData();
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        bannerRunnableHandler.removeCallbacks(bannerAutoSlideRunnable);
        isSlidingBanner = false;
    }

    // ---------------------------------------------------


    private void initView() {

        refreshLayout.setOnRefreshListener(() -> {
            updateData();
            refreshLayout.setRefreshing(false);
        });

        bannerAdapter = new BannerVPAdapter();
        vpBanner.setAdapter(bannerAdapter);
        vpBanner.setOffscreenPageLimit(1);
        vpBanner.setPageTransformer(new CustomViewPagerTransformer());
        vpBanner.addItemDecoration(new HorizontalMarginItemDecoration());

        setupRecyclerView(rclvHotGame, new GameMinInfoRecyclerViewAdapter(), setUpLayoutManager(2));
        setupRecyclerView(rclvNewGame, new GameInfoRecyclerViewAdapter(), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvEditorChoice, new GameMinInfoRecyclerViewAdapter(), setUpLayoutManager(2));
        setupRecyclerView(rclvTrending, new GameMinInfoRecyclerViewAdapter(), setUpLayoutManager(2));

        viewAllHotGameImb.setOnClickListener(this);
        viewAllTrendingImb.setOnClickListener(this);
        viewAllEditorChoiceImb.setOnClickListener(this);
    }

    private void bindData() {

        homeViewModel.getBannerList().observe(getViewLifecycleOwner(), banners -> {
            bannerAdapter.bindData(banners);
            if (banners.size() != 0 && !isSlidingBanner)
                bannerRunnableHandler.postDelayed(bannerAutoSlideRunnable, 3000);
        });

        homeViewModel.getHotGameList().observe(getViewLifecycleOwner(), games -> {
            viewAllHotGameImb.setVisibility(games == null || games.size() == 0 ? View.GONE : View.VISIBLE);
            errHotGameImv.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameMinInfoRecyclerViewAdapter) Objects.requireNonNull(rclvHotGame.getAdapter())).bindData(games);
        });

        homeViewModel.getNewGameList().observe(getViewLifecycleOwner(), games -> {
            //viewAllNewImb.setVisibility(games == null || games.size() == 0 ? View.GONE : View.VISIBLE);
            ((GameInfoRecyclerViewAdapter) Objects.requireNonNull(rclvNewGame.getAdapter())).bindData(games);
        });

        homeViewModel.getEditorChoiceList().observe(getViewLifecycleOwner(), games -> {
            viewAllEditorChoiceImb.setVisibility(games == null || games.size() == 0 ? View.GONE : View.VISIBLE);
            errEditorChoiceImv.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameMinInfoRecyclerViewAdapter) Objects.requireNonNull(rclvEditorChoice.getAdapter())).bindData(games);
        });

        homeViewModel.getTrendingList().observe(getViewLifecycleOwner(), games -> {
            viewAllTrendingImb.setVisibility(games == null || games.size() == 0 ? View.GONE : View.VISIBLE);
            errTrendingImv.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameMinInfoRecyclerViewAdapter) Objects.requireNonNull(rclvTrending.getAdapter())).bindData(games);
        });

        updateData();

    }

    private void updateData() {
        homeViewModel.getBannerListData();
        homeViewModel.getNewGameListData();
        homeViewModel.getRecentPlayData();
        homeViewModel.getEditorsChoiceListData();
        homeViewModel.getHotGameListData();
        homeViewModel.getTrendingListData();
    }

    private void setupRecyclerView(RecyclerView view, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        view.setAdapter(adapter);
        view.setLayoutManager(layoutManager);
        view.setHasFixedSize(true);
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


    //----------------- EVENT ---------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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