package com.rora.phase.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.rora.phase.model.Game;
import com.rora.phase.model.enums.PayTypeEnum;
import com.rora.phase.model.ui.HomeUIData;
import com.rora.phase.ui.adapter.BannerVPAdapter;
import com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter;
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

import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.MEDIUM_SIZE;
import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.NORMAL_SIZE;

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView rclMain, rclvDiscover, rclvCategoryDiscover, rclvCategory;
    private SwipeRefreshLayout refreshLayout;
    private ViewPager2 vpBanner, vpOtherGames;
    private TabLayout tbOtherGames;

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

        rclMain = root.findViewById(R.id.main_rclv_home_screen);
        rclvCategory = root.findViewById(R.id.category_rclv);
        rclvDiscover = root.findViewById(R.id.discover_rclv);
        rclvCategoryDiscover = root.findViewById(R.id.category_discover_rclv);

        initView(root);
        bindData();

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

        HomeRVAdapter homeAdapter = new HomeRVAdapter(getContext());
        rclMain.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rclMain.setAdapter(homeAdapter);
        rclMain.setHasFixedSize(true);

        homeAdapter.setOnViewAllClickListener(selectedItem -> {
            HomeViewModel.GameListType gameListType = HomeViewModel.GameListType.fromHomeListType(selectedItem.type);
            if (gameListType != null);
                goToGameListScreen(selectedItem.getSessionName(getActivity()), gameListType, "");
        });
        homeAdapter.setOnChildItemClickListener(selectedItem -> {
            moveTo(GameDetailFragment.newInstance((Game) selectedItem), GameDetailFragment.class.getSimpleName());
        });

        setupGameRecyclerView(rclvDiscover, new GameMinInfoRecyclerViewAdapter(GameMinInfoRecyclerViewAdapter.VIEW_TYPE_NORMAL, 0), ViewHelper.getLayoutManager(getActivity(), 2, 0));

        CategoryRecyclerViewAdapter categoryAdapter =  new CategoryRecyclerViewAdapter(0.24, NORMAL_SIZE, true,
                selectedItemId -> goToGameListScreen((String)selectedItemId, HomeViewModel.GameListType.BY_CATEGORY, (String)selectedItemId));
        setupRecyclerView(rclvCategory, categoryAdapter, new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        CategoryRecyclerViewAdapter categoryDiscoveryAdapter =  new CategoryRecyclerViewAdapter(0, MEDIUM_SIZE, false,
                selectedItemId -> homeViewModel.getGamesDataByType(HomeViewModel.GameListType.BY_CATEGORY, (String) selectedItemId));
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
            if (!stopUpDateHomeScreen)
                ((HomeRVAdapter)rclMain.getAdapter()).bindData(new HomeUIData(HomeUIData.Type.NEW, games));
        });

        homeViewModel.getTrendingList().observe(getViewLifecycleOwner(), games -> {
            if (!stopUpDateHomeScreen)
                ((HomeRVAdapter)rclMain.getAdapter()).bindData(new HomeUIData(HomeUIData.Type.TRENDING, games));
        });

        homeViewModel.getHotGameList().observe(getViewLifecycleOwner(), games -> {
            if (!stopUpDateHomeScreen)
                ((HomeRVAdapter)rclMain.getAdapter()).bindData(new HomeUIData(HomeUIData.Type.HOT, games));
        });

        homeViewModel.getEditorChoiceList().observe(getViewLifecycleOwner(), games -> {
            if (!stopUpDateHomeScreen)
                ((HomeRVAdapter)rclMain.getAdapter()).bindData(new HomeUIData(HomeUIData.Type.EDITOR, games));
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

        adapter.setOnItemSelectedListener(selectedItem -> moveTo(GameDetailFragment.newInstance((Game) selectedItem), GameDetailFragment.class.getSimpleName()));
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
            case R.id.btn_view_all_discover:
                if (rclvDiscover.getAdapter().getItemCount() == 0)
                    break;
                goToGameListScreen(homeViewModel.getCurrentSelectedItemId(), HomeViewModel.GameListType.BY_CATEGORY, homeViewModel.getCurrentSelectedItemId());
                break;
            default: break;
        }
    }

}