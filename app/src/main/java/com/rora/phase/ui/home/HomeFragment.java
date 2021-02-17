package com.rora.phase.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.MEDIUM_SIZE;
import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.NORMAL_SIZE;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView rclvHotGame, rclvNewGame, rclvTrending, rclvEditorChoice, rclvDiscover, rclvCategoryDiscover, rclvCategory;
    private SwipeRefreshLayout refreshLayout;
    private ViewPager2 vpBanner, vpOtherGames;
    private TabLayout tbOtherGames;
    private ImageView imvErrEditorChoice, imvErrTrending, imvErrHotGame, imvErrDiscover;

    private HomeViewModel homeViewModel;
    private BannerVPAdapter bannerAdapter;
    private TabPagerAdapter otherGamesAdapter;
    private boolean isSlidingBanner = false;

    private Handler bannerRunnableHandler = new Handler();
    private int autoScrollBannerTime = 5000;
    private Runnable bannerAutoSlideRunnable = new Runnable() {
        @Override
        public void run() {
            isSlidingBanner = true;
            int currentPos = vpBanner.getCurrentItem();
            vpBanner.setCurrentItem(currentPos == bannerAdapter.getItemCount()-1 ? 0 : currentPos+1);
            bannerRunnableHandler.postDelayed(this, autoScrollBannerTime);
        }
    };


    //------------------------ LIFECYCLE --------------------------

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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

        initView();
        bindData();

        root.findViewById(R.id.btn_view_all_new_game).setOnClickListener(this);
        root.findViewById(R.id.btn_view_all_hot_game).setOnClickListener(this);
        root.findViewById(R.id.btn_view_all_trending).setOnClickListener(this);
        root.findViewById(R.id.btn_view_all_editor_choice).setOnClickListener(this);
        root.findViewById(R.id.btn_view_all_discover).setOnClickListener(this);
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

        List<Bundle> params = new ArrayList<>();
        if (homeViewModel.isUserLogged()) {
            Bundle bundleTab1 = new Bundle();
            bundleTab1.putString(TabPagerAdapter.TAB_TITLE, getResources().getString(R.string.recommended_title));
            bundleTab1.putSerializable(GameListFragment.LIST_TYPE_PARAM, HomeViewModel.GameListType.RECOMMENDED);
            params.add(bundleTab1);
        }

        Bundle bundleTab2 = new Bundle();
        bundleTab2.putString(TabPagerAdapter.TAB_TITLE, getResources().getString(R.string.free_to_play_title));
        bundleTab2.putSerializable(GameListFragment.LIST_TYPE_PARAM, HomeViewModel.GameListType.BY_PAY_TYPE);
        bundleTab2.putString(GameListFragment.KEY_FILTER_PARAM, PayTypeEnum.FREE.toString());
        params.add(bundleTab2);

        otherGamesAdapter = new TabPagerAdapter(this, params);
        vpOtherGames.setAdapter(otherGamesAdapter);
        new TabLayoutMediator(tbOtherGames, vpOtherGames, ((tab, position) -> tab.setText(params.get(position).getString(TabPagerAdapter.TAB_TITLE)))).attach();
        ViewGroup.LayoutParams layoutParams = vpOtherGames.getLayoutParams();

        int height = ((AppCompatActivity)getContext()).getWindowManager().getDefaultDisplay().getHeight();
        layoutParams.height = height - ((AppCompatActivity) getContext()).getSupportActionBar().getHeight() - tbOtherGames.getHeight() - 300;
        vpOtherGames.setLayoutParams(layoutParams);

        setupRecyclerView(rclvHotGame, new GameMinInfoRecyclerViewAdapter(GameMinInfoRecyclerViewAdapter.VIEW_TYPE_EXPANDED, 0.8), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvNewGame, new GameInfoRecyclerViewAdapter(GameInfoRecyclerViewAdapter.VIEW_TYPE_NORMAL, 0.75), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvEditorChoice, new GameInfoRecyclerViewAdapter(GameInfoRecyclerViewAdapter.VIEW_TYPE_LAYER, 0.9), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvTrending, new GameMinInfoRecyclerViewAdapter(GameMinInfoRecyclerViewAdapter.VIEW_TYPE_EXPANDED, 0.8), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvDiscover, new GameMinInfoRecyclerViewAdapter(GameMinInfoRecyclerViewAdapter.VIEW_TYPE_NORMAL, 0), ViewHelper.getLayoutManager(getActivity(), 2, 0));

        setupRecyclerView(rclvCategory, new CategoryRecyclerViewAdapter(new OnItemSelectedListener() {
            @Override
            public void onSelected(String selectedItemId) {
                goToGameListScreen(selectedItemId, HomeViewModel.GameListType.BY_CATEGORY, selectedItemId);
            }
        }, 0.24, NORMAL_SIZE, true), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        setupRecyclerView(rclvCategoryDiscover, new CategoryRecyclerViewAdapter(new OnItemSelectedListener() {
            @Override
            public void onSelected(String selectedItemId) {
                homeViewModel.getGamesByType(HomeViewModel.GameListType.BY_CATEGORY, selectedItemId);
            }
        }, 0, MEDIUM_SIZE, false), ViewHelper.getLayoutManager(getActivity(), 5, 0));

    }

    private void bindData() {

        homeViewModel.getBannerList().observe(getViewLifecycleOwner(), banners -> {
            bannerAdapter.bindData(banners);
            if (banners.size() != 0 && !isSlidingBanner)
                bannerRunnableHandler.postDelayed(bannerAutoSlideRunnable, autoScrollBannerTime);
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
            ((GameInfoRecyclerViewAdapter) Objects.requireNonNull(rclvEditorChoice.getAdapter())).bindData(games);
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
        homeViewModel.getCategoryListData();
        homeViewModel.getGamesByType(HomeViewModel.GameListType.NEW, null);
        homeViewModel.getGamesByType(HomeViewModel.GameListType.EDITOR, null);
        homeViewModel.getGamesByType(HomeViewModel.GameListType.HOT, null);
        homeViewModel.getGamesByType(HomeViewModel.GameListType.TRENDING, null);
    }

    private void setupRecyclerView(RecyclerView view, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        view.setAdapter(adapter);
        view.setLayoutManager(layoutManager);
        view.setHasFixedSize(true);
    }

    private void goToGameListScreen(String title, HomeViewModel.GameListType type, String filterParam) {
        Bundle listGameBundle = new Bundle();
        listGameBundle.putString(GameListFragment.SCREEN_TITLE_PARAM, title);
        listGameBundle.putSerializable(GameListFragment.LIST_TYPE_PARAM, type);
        listGameBundle.putString(GameListFragment.KEY_FILTER_PARAM, filterParam);
        homeViewModel.refresh(null);
        NavHostFragment.findNavController(this).navigate(R.id.action_navigation_home_to_gameListFragment, listGameBundle);
    }

    private void goToGameDetails(String game) {
        NavHostFragment.findNavController(this).navigate(R.id.action_navigation_home_to_gameDetailFragment);
    }

    //----------------- EVENT ---------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_view_all_new_game:
                if (rclvNewGame.getAdapter().getItemCount() == 0)
                    break;
                goToGameListScreen(getResources().getString(R.string.new_game_title), HomeViewModel.GameListType.NEW, "");
                break;
            case R.id.btn_view_all_hot_game:
                if (rclvHotGame.getAdapter().getItemCount() == 0)
                    break;
                goToGameListScreen(getResources().getString(R.string.hot_title), HomeViewModel.GameListType.HOT, "");
                break;
            case R.id.btn_view_all_trending:
                if (rclvTrending.getAdapter().getItemCount() == 0)
                    break;
                goToGameListScreen(getResources().getString(R.string.trending_title), HomeViewModel.GameListType.TRENDING, "");
                break;
            case R.id.btn_view_all_editor_choice:
                if (rclvEditorChoice.getAdapter().getItemCount() == 0)
                    break;
                goToGameListScreen(getResources().getString(R.string.editor_choice_title), HomeViewModel.GameListType.EDITOR, "");
                break;
            case R.id.btn_view_all_discover:
                if (rclvDiscover.getAdapter().getItemCount() == 0)
                    break;
                goToGameListScreen(homeViewModel.getCurrentSelectedItemId(), HomeViewModel.GameListType.BY_CATEGORY, homeViewModel.getCurrentSelectedItemId());
                break;
            default: break;
        }
    }

}