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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.rora.phase.ui.adapter.CategoryRVAdapter;
import com.rora.phase.ui.adapter.GameRVAdapter;
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

import static android.view.View.GONE;
import static com.rora.phase.ui.adapter.CategoryRVAdapter.MEDIUM_SIZE;
import static com.rora.phase.ui.adapter.CategoryRVAdapter.NORMAL_SIZE;

public class HomeFragment extends BaseFragment {

    private RecyclerView rclMain, rclvCategory;
    private SwipeRefreshLayout refreshLayout;
    private ViewPager2 vpBanner, vpOtherGames;
    private TabLayout tbOtherGames;

    private HomeViewModel homeViewModel;
    private BannerVPAdapter bannerAdapter;
    private TabPagerAdapter otherGamesAdapter;
    private ImageView bannerErrImv;
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

    //------------------------ LIFECYCLE --------------------------

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showLoadingScreen();
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        refreshLayout = root.findViewById(R.id.refresh_layout_home_screen);
        vpBanner = root.findViewById(R.id.banner_vp);
        vpOtherGames = root.findViewById(R.id.other_games_vp);
        tbOtherGames = root.findViewById(R.id.other_games_tab_layout);
        bannerErrImv = root.findViewById(R.id.error_banner_home_imv);

        rclMain = root.findViewById(R.id.main_rclv_home_screen);
        rclvCategory = root.findViewById(R.id.category_rclv);

        initView(root);
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


    private void initView(View root) {
        showActionbar(root, getResources().getString(R.string.app_label), false);
        setActionbarStyle(getResources().getDimension(R.dimen.logo_text_size), ContextCompat.getColor(getContext(), R.color.colorPrimary));

        refreshLayout.setOnRefreshListener(() -> {
            updateData();
            refreshLayout.setRefreshing(false);
        });

        setupBannerView();

        HomeRVAdapter homeAdapter = new HomeRVAdapter(getActivity());
        rclMain.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rclMain.setAdapter(homeAdapter);
        rclMain.setHasFixedSize(true);

        homeAdapter.setOnViewAllClickListener((position, selectedItem) -> {
            HomeViewModel.GameListType gameListType = HomeViewModel.GameListType.getTypeFromHomeListType(selectedItem.type);
            if (gameListType != null) {
                if (gameListType == HomeViewModel.GameListType.BY_CATEGORY) {
                    goToGameListScreen(homeViewModel.getCurrentSelectedItemId(), HomeViewModel.GameListType.BY_CATEGORY, homeViewModel.getCurrentSelectedItemId());
                } else {
                    goToGameListScreen(selectedItem.getSessionName(getActivity()), gameListType, "");
                }
            }

        });
        homeAdapter.setOnChildItemClickListener((position, selectedItem) -> moveTo(GameDetailFragment.newInstance((Game) selectedItem), GameDetailFragment.class.getSimpleName(), true));

        homeAdapter.setOnCategoryClickListener((position, selectedItemId) -> homeViewModel.getGamesDataByType(HomeViewModel.GameListType.BY_CATEGORY, (String) selectedItemId));

        CategoryRVAdapter categoryAdapter =  new CategoryRVAdapter(NORMAL_SIZE, true);
        setupRecyclerView(rclvCategory, categoryAdapter, new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter.setOnItemSelectedListener((position, selectedItemId) -> goToGameListScreen((String)selectedItemId, HomeViewModel.GameListType.BY_CATEGORY, (String)selectedItemId));
    }

    private void bindData() {
        homeViewModel.getBannerList().observe(getViewLifecycleOwner(), banners -> {
            bannerAdapter.bindData(banners);

            bannerRunnableHandler.removeCallbacks(bannerAutoSlideRunnable);
            isSlidingBanner = false;
            if (banners.size() == 0) {
                bannerErrImv.setVisibility(View.VISIBLE);
                return;
            }

            bannerErrImv.setVisibility(GONE);
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
                ((HomeRVAdapter)rclMain.getAdapter()).bindDataWithCategoryData(new HomeUIData(HomeUIData.Type.DISCOVER_BY_CATEGORY, null, tags));
                ((CategoryRVAdapter) Objects.requireNonNull(rclvCategory.getAdapter())).bindData(tags);
            }
            hideLoadingScreen();
        });

        updateData();
    }

    private void updateData() {
        stopUpDateHomeScreen = false;
        homeViewModel.getBannerListData();
        homeViewModel.getGamesDataByType(HomeViewModel.GameListType.TRENDING, null);
        homeViewModel.getGamesDataByType(HomeViewModel.GameListType.HOT, null);
        homeViewModel.getGamesDataByType(HomeViewModel.GameListType.EDITOR, null);
        homeViewModel.getGamesDataByType(HomeViewModel.GameListType.NEW, null);
        homeViewModel.getCategoryListData();
    }

    private void setupBannerView() {
        bannerAdapter = new BannerVPAdapter(0);
        vpBanner.setAdapter(bannerAdapter);
        vpBanner.setOffscreenPageLimit(2);
        vpBanner.setPageTransformer(new CustomViewPagerTransformer());
        vpBanner.addItemDecoration(new HorizontalMarginItemDecoration());

        otherGamesAdapter = new TabPagerAdapter(this);
        otherGamesAdapter.addPage(getResources().getString(R.string.free_to_play_title), GameListFragment.newInstance(null, HomeViewModel.GameListType.BY_PAY_TYPE, PayTypeEnum.FREE.toString()));
        if (homeViewModel.isUserLogged())
            otherGamesAdapter.addPage(getResources().getString(R.string.recommended_title), GameListFragment.newInstance(null, HomeViewModel.GameListType.RECOMMENDED, null));

        vpOtherGames.setAdapter(otherGamesAdapter);

        new TabLayoutMediator(tbOtherGames, vpOtherGames, ((tab, position) -> tab.setText(otherGamesAdapter.getTitle(position)))).attach();
        ViewGroup.LayoutParams layoutParams = vpOtherGames.getLayoutParams();

        //Calculate size
        int height = ((AppCompatActivity)getContext()).getWindowManager().getDefaultDisplay().getHeight();
        layoutParams.height = height - getActionBar().getHeight() - tbOtherGames.getHeight() - 300;
        vpOtherGames.setLayoutParams(layoutParams);
    }

    private void setupRecyclerView(RecyclerView view, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        view.setAdapter(adapter);
        view.setLayoutManager(layoutManager);
        view.setHasFixedSize(true);
    }

    private void goToGameListScreen(String title, HomeViewModel.GameListType type, String filterParam) {
        homeViewModel.refresh(null);

        stopUpDateHomeScreen = true;
        moveTo(GameListFragment.newInstance(title, type, filterParam), GameListFragment.class.getSimpleName(), true);
    }

    //----------------- EVENT ---------------

}