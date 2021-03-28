package com.rora.phase.ui.home;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
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
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.ui.game.GameListFragment;
import com.rora.phase.ui.viewmodel.HomeViewModel;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.CustomViewPagerTransformer;
import com.rora.phase.utils.ui.HorizontalMarginItemDecoration;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes;
import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.MEDIUM_SIZE;
import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.NORMAL_SIZE;

public class HomeFragment extends BaseFragment implements View.OnClickListener {

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
    private int autoScrollBannerTime = 3500;
    private Runnable bannerAutoSlideRunnable = new Runnable() {
        @Override
        public void run() {
            isSlidingBanner = true;
            int currentPos = vpBanner.getCurrentItem();
            vpBanner.setCurrentItem(currentPos == bannerAdapter.getItemCount()-1 ? 0 : currentPos+1);
            bannerRunnableHandler.postDelayed(this, autoScrollBannerTime);
        }
    };

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //------------------------ LIFECYCLE --------------------------

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //NavController navController = Navigation.findNavController(view);
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //Toolbar toolbar = view.findViewById(R.id.toolbar);
        //
        ////NavigationUI.setupWithNavController( toolbar, navController, appBarConfiguration);
        //toolbar.setTitle(getResources().getString(R.string.app_label));
        //toolbar.setTitleTextColor(getActivity().getColor(R.color.colorPrimary));
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showLoadingScreen();
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

        initView(root);
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


    private void initView(View root) {
        showActionbar(root, getResources().getString(R.string.app_label), false);
        setActionbarStyle(root, getResources().getDimension(R.dimen.logo_text_size), ContextCompat.getColor(getContext(), R.color.colorPrimary));

        refreshLayout.setOnRefreshListener(() -> {
            updateData();
            refreshLayout.setRefreshing(false);
        });

        setupBannerView();

        setupGameRecyclerView(rclvHotGame, new GameMinInfoRecyclerViewAdapter(GameMinInfoRecyclerViewAdapter.VIEW_TYPE_EXPANDED, 0.8), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupGameRecyclerView(rclvNewGame, new GameInfoRecyclerViewAdapter(GameInfoRecyclerViewAdapter.VIEW_TYPE_NORMAL, 0.75), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setupGameRecyclerView(rclvEditorChoice, new GameInfoRecyclerViewAdapter(GameInfoRecyclerViewAdapter.VIEW_TYPE_LAYER, 0.9), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupGameRecyclerView(rclvTrending, new GameMinInfoRecyclerViewAdapter(GameMinInfoRecyclerViewAdapter.VIEW_TYPE_EXPANDED, 0.8), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupGameRecyclerView(rclvDiscover, new GameMinInfoRecyclerViewAdapter(GameMinInfoRecyclerViewAdapter.VIEW_TYPE_NORMAL, 0), ViewHelper.getLayoutManager(getActivity(), 2, 0));

        CategoryRecyclerViewAdapter categoryAdapter =  new CategoryRecyclerViewAdapter(0.24, NORMAL_SIZE, true,
                selectedItemId -> goToGameListScreen(selectedItemId, HomeViewModel.GameListType.BY_CATEGORY, selectedItemId));
        setupRecyclerView(rclvCategory, categoryAdapter, new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        CategoryRecyclerViewAdapter categoryDiscoveryAdapter =  new CategoryRecyclerViewAdapter(0, MEDIUM_SIZE, false,
                selectedItemId -> homeViewModel.getGamesDataByType(HomeViewModel.GameListType.BY_CATEGORY, selectedItemId));
        setupRecyclerView(rclvCategoryDiscover, categoryDiscoveryAdapter, ViewHelper.getLayoutManager(getActivity(), 5, 0));
    }

    private void bindData() {
        homeViewModel.getBannerList().observe(getViewLifecycleOwner(), banners -> {
            bannerAdapter.bindData(banners);

            bannerRunnableHandler.removeCallbacks(bannerAutoSlideRunnable);
            isSlidingBanner = false;
            if (banners.size() != 0)
                bannerRunnableHandler.postDelayed(bannerAutoSlideRunnable, autoScrollBannerTime);
        });

        homeViewModel.getNewGameList().observe(getViewLifecycleOwner(), games -> {
            if (!stopUpDateHomeScreen) {
                //imvErrNew.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
                ((GameInfoRecyclerViewAdapter) Objects.requireNonNull(rclvNewGame.getAdapter())).bindData(games);
            }
        });

        homeViewModel.getTrendingList().observe(getViewLifecycleOwner(), games -> {
            if (!stopUpDateHomeScreen) {
                imvErrTrending.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
                ((GameMinInfoRecyclerViewAdapter) Objects.requireNonNull(rclvTrending.getAdapter())).bindData(games);
            }
        });

        homeViewModel.getHotGameList().observe(getViewLifecycleOwner(), games -> {
            if (!stopUpDateHomeScreen) {
                imvErrHotGame.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
                ((GameMinInfoRecyclerViewAdapter) Objects.requireNonNull(rclvHotGame.getAdapter())).bindData(games);
            }
        });

        homeViewModel.getEditorChoiceList().observe(getViewLifecycleOwner(), games -> {
            if (!stopUpDateHomeScreen) {
                imvErrEditorChoice.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
                ((GameInfoRecyclerViewAdapter) Objects.requireNonNull(rclvEditorChoice.getAdapter())).bindData(games);
            }
        });

        homeViewModel.getCategoryList().observe(getViewLifecycleOwner(), tags -> {
            if (!stopUpDateHomeScreen) {
                //imvErrTrending.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
                ((CategoryRecyclerViewAdapter) Objects.requireNonNull(rclvCategoryDiscover.getAdapter())).bindData(tags);
                ((CategoryRecyclerViewAdapter) Objects.requireNonNull(rclvCategory.getAdapter())).bindData(tags);
            }
        });

        homeViewModel.getGameByCategoryList().observe(getViewLifecycleOwner(), games -> {
            if (!stopUpDateHomeScreen) {
                //imvErrTrending.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
                ((GameMinInfoRecyclerViewAdapter) Objects.requireNonNull(rclvDiscover.getAdapter())).bindData(games);
            }
            hideLoadingScreen();
        });

        updateData();
    }

    private void updateData() {
        stopUpDateHomeScreen = false;
        homeViewModel.getBannerListData();
        homeViewModel.getCategoryListData();
        homeViewModel.getGamesDataByType(HomeViewModel.GameListType.TRENDING, null);
        homeViewModel.getGamesDataByType(HomeViewModel.GameListType.HOT, null);
        homeViewModel.getGamesDataByType(HomeViewModel.GameListType.EDITOR, null);
        homeViewModel.getGamesDataByType(HomeViewModel.GameListType.NEW, null);
    }

    private void setupBannerView() {
        bannerAdapter = new BannerVPAdapter(0);
        vpBanner.setAdapter(bannerAdapter);
        vpBanner.setOffscreenPageLimit(2);
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

        //Calculate size
        int height = ((AppCompatActivity)getContext()).getWindowManager().getDefaultDisplay().getHeight();
        layoutParams.height = height - getActionBar().getHeight() - tbOtherGames.getHeight() - 300;
        vpOtherGames.setLayoutParams(layoutParams);
    }

    private void setupGameRecyclerView(RecyclerView view, BaseRVAdapter adapter, RecyclerView.LayoutManager layoutManager) {
        view.setAdapter(adapter);
        view.setLayoutManager(layoutManager);
        view.setHasFixedSize(true);

        adapter.setOnItemSelectedListener(selectedItemId -> moveTo(GameDetailFragment.newInstance(selectedItemId), GameDetailFragment.class.getSimpleName()));
    }

    private void setupRecyclerView(RecyclerView view, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        view.setAdapter(adapter);
        view.setLayoutManager(layoutManager);
        view.setHasFixedSize(true);
    }

    private void goToGameListScreen(String title, HomeViewModel.GameListType type, String filterParam) {
        homeViewModel.refresh(null);

        stopUpDateHomeScreen = true;
        moveTo(GameListFragment.newInstance(title, type, filterParam), GameListFragment.class.getSimpleName());
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