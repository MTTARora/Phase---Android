package com.rora.phase.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rora.phase.R;
import com.rora.phase.model.enums.PayTypeEnum;
import com.rora.phase.ui.adapter.BannerVPAdapter;
import com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter;
import com.rora.phase.ui.adapter.GameInfoRecyclerViewAdapter;
import com.rora.phase.ui.adapter.GameMinInfoRecyclerViewAdapter;
import com.rora.phase.ui.adapter.TabPagerAdapter;
import com.rora.phase.ui.viewmodel.HomeViewModel;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.CustomViewPagerTransformer;
import com.rora.phase.utils.ui.HorizontalMarginItemDecoration;

import java.util.Objects;

import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.MEDIUM_SIZE;
import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.NORMAL_SIZE;

public class HomeFragment extends Fragment implements View.OnClickListener, OnItemSelectedListener {

    private RecyclerView rclvHotGame, rclvNewGame, rclvTrending, rclvEditorChoice, rclvDiscover, rclvCategoryDiscover, rclvCategory;
    private SwipeRefreshLayout refreshLayout;
    private ViewPager2 vpBanner, vpOtherGames;
    private TabLayout tbOtherGames;
    private Button btnViewAllNew, btnViewAllEditorChoice, btnViewAllTrending, btnViewAllHotGame, btnViewAllDiscover;
    private ImageView imvErrEditorChoice, imvErrTrending, imvErrHotGame, imvErrDiscover;

    private HomeViewModel homeViewModel;
    private BannerVPAdapter bannerAdapter;
    private TabPagerAdapter otherGamesAdapter;
    private boolean isSlidingBanner = false;

    private Handler bannerRunnableHandler = new Handler();
    private Runnable bannerAutoSlideRunnable = new Runnable() {
        @Override
        public void run() {
            isSlidingBanner = true;
            int currentPos = vpBanner.getCurrentItem();
            vpBanner.setCurrentItem(currentPos == bannerAdapter.getItemCount()-1 ? 0 : currentPos+1);
            bannerRunnableHandler.postDelayed(this, 5000);
        }
    };


    //------------------------ LIFECYCLE --------------------------


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        NavigationUI.setupWithNavController( toolbar, navController, appBarConfiguration);
        toolbar.setTitle("Phase");
        toolbar.setTitleTextColor(getActivity().getColor(R.color.colorPrimary));
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        refreshLayout = root.findViewById(R.id.refresh_layout_home_screen);
        vpBanner = root.findViewById(R.id.banner_vp);
        vpOtherGames = root.findViewById(R.id.other_games_vp);
        tbOtherGames = root.findViewById(R.id.other_games_tab_layout);

        rclvCategory = root.findViewById(R.id.category_rclv);
        rclvNewGame = root.findViewById(R.id.new_game_rclv_home_screen);
        rclvEditorChoice = root.findViewById(R.id.editor_choice_rclv_home_screen);
        rclvTrending = root.findViewById(R.id.trending_rclv_home_screen);
        rclvHotGame = root.findViewById(R.id.hot_game_rclv_home_screen);
        rclvDiscover = root.findViewById(R.id.discover_rclv);
        rclvCategoryDiscover = root.findViewById(R.id.category_discover_rclv);

        imvErrEditorChoice = root.findViewById(R.id.error_data_editor_choice_imv);
        imvErrTrending = root.findViewById(R.id.error_data_trending_imv);
        imvErrHotGame = root.findViewById(R.id.error_data_hot_game_imv);

        btnViewAllNew = root.findViewById(R.id.btn_view_all_new_game);
        btnViewAllEditorChoice = root.findViewById(R.id.btn_view_all_editor_choice);
        btnViewAllTrending = root.findViewById(R.id.btn_view_all_trending);
        btnViewAllHotGame = root.findViewById(R.id.btn_view_all_hot_game);
        btnViewAllDiscover = root.findViewById(R.id.btn_view_all_discover);

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

        otherGamesAdapter = new TabPagerAdapter(this);
        vpOtherGames.setAdapter(otherGamesAdapter);
        new TabLayoutMediator(tbOtherGames, vpOtherGames, ((tab, position) -> tab.setText(position == 1 ? "Recommended" : "Free to play"))).attach();
        ViewGroup.LayoutParams layoutParams = vpOtherGames.getLayoutParams();

        int height = ((AppCompatActivity)getContext()).getWindowManager().getDefaultDisplay().getHeight();
        layoutParams.height = height - ((AppCompatActivity) getContext()).getSupportActionBar().getHeight() - tbOtherGames.getHeight() - 300;
        vpOtherGames.setLayoutParams(layoutParams);

        setupRecyclerView(rclvCategory, new CategoryRecyclerViewAdapter(this, 0.24, NORMAL_SIZE, true), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvHotGame, new GameMinInfoRecyclerViewAdapter(R.layout.item_game_min_info_expanded, 0.8), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvNewGame, new GameInfoRecyclerViewAdapter(), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvEditorChoice, new GameMinInfoRecyclerViewAdapter(R.layout.item_game_min_info_expanded, 0.8), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvTrending, new GameMinInfoRecyclerViewAdapter(R.layout.item_game_min_info_expanded, 0.8), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvCategoryDiscover, new CategoryRecyclerViewAdapter(this, 0, MEDIUM_SIZE, false), setUpLayoutManager(5));
        setupRecyclerView(rclvDiscover, new GameMinInfoRecyclerViewAdapter(R.layout.item_game_min_info, 0), setUpLayoutManager(2));

        btnViewAllHotGame.setOnClickListener(this);
        btnViewAllTrending.setOnClickListener(this);
        btnViewAllEditorChoice.setOnClickListener(this);
        btnViewAllDiscover.setOnClickListener(this);

    }

    private void bindData() {

        homeViewModel.getBannerList().observe(getViewLifecycleOwner(), banners -> {
            bannerAdapter.bindData(banners);
            if (banners.size() != 0 && !isSlidingBanner)
                bannerRunnableHandler.postDelayed(bannerAutoSlideRunnable, 3000);
        });

        homeViewModel.getHotGameList().observe(getViewLifecycleOwner(), games -> {
            imvErrHotGame.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameMinInfoRecyclerViewAdapter) Objects.requireNonNull(rclvHotGame.getAdapter())).bindData(games);
        });

        homeViewModel.getNewGameList().observe(getViewLifecycleOwner(), games -> {
            //imvErrNew.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameInfoRecyclerViewAdapter) Objects.requireNonNull(rclvNewGame.getAdapter())).bindData(games);
        });

        homeViewModel.getEditorChoiceList().observe(getViewLifecycleOwner(), games -> {
            imvErrEditorChoice.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameMinInfoRecyclerViewAdapter) Objects.requireNonNull(rclvEditorChoice.getAdapter())).bindData(games);
        });

        homeViewModel.getTrendingList().observe(getViewLifecycleOwner(), games -> {
            imvErrTrending.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameMinInfoRecyclerViewAdapter) Objects.requireNonNull(rclvTrending.getAdapter())).bindData(games);
        });

        homeViewModel.getCategoryList().observe(getViewLifecycleOwner(), tags -> {
            //imvErrTrending.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((CategoryRecyclerViewAdapter) Objects.requireNonNull(rclvCategoryDiscover.getAdapter())).bindData(tags);
            ((CategoryRecyclerViewAdapter) Objects.requireNonNull(rclvCategory.getAdapter())).bindData(tags);
        });

        homeViewModel.getGameByCategoryList().observe(getViewLifecycleOwner(), games -> {
            //imvErrTrending.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameMinInfoRecyclerViewAdapter) Objects.requireNonNull(rclvDiscover.getAdapter())).bindData(games);
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
        homeViewModel.getCategoryListData();
        homeViewModel.getRecommendedGameListData();
        homeViewModel.getGameByPayTypeListData(PayTypeEnum.FREE.id);
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

    @Override
    public void onSelected(String selectedItemId) {
        homeViewModel.getGamesByCategoryListData(selectedItemId);
    }

}