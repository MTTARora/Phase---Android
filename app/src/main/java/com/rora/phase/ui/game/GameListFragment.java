package com.rora.phase.ui.game;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rora.phase.R;
import com.rora.phase.ui.adapter.GameVerticalRVAdapter;
import com.rora.phase.ui.viewmodel.HomeViewModel;
import com.rora.phase.utils.callback.ILoadMore;
import com.rora.phase.utils.ui.BaseFragment;

import java.util.Objects;

public class GameListFragment extends BaseFragment {

    private RecyclerView rclvGameList;

    private HomeViewModel homeViewModel;
    private HomeViewModel.GameListType listType;
    private String screenTitle;
    private String filterParam = "";

    public static final String SCREEN_TITLE_PARAM = "screen_title";
    public static final String LIST_TYPE_PARAM = "type";
    public static final String KEY_FILTER_PARAM = "filter";

    public GameListFragment() {}

    public static GameListFragment newInstance(String screenTitle, HomeViewModel.GameListType type, String filterParam) {
        GameListFragment fragment = new GameListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SCREEN_TITLE_PARAM, screenTitle);
        bundle.putSerializable(LIST_TYPE_PARAM, type);
        bundle.putString(KEY_FILTER_PARAM, filterParam);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        if (getArguments() != null) {
            screenTitle = getArguments().getString(SCREEN_TITLE_PARAM);
            filterParam = getArguments().getString(KEY_FILTER_PARAM);
            listType = (HomeViewModel.GameListType) getArguments().getSerializable(LIST_TYPE_PARAM);
        }

        View root = inflater.inflate(R.layout.fragment_game_list, container, false);

        rclvGameList = root.findViewById(R.id.game_list_vertical_rclv);

        initView();
        bindData(root);

        return root;
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL , false);
        rclvGameList.setLayoutManager(linearLayoutManager);
        GameVerticalRVAdapter adapter = new GameVerticalRVAdapter(rclvGameList);
        rclvGameList.setAdapter(adapter);
        adapter.setOnItemSelectedListener(this::goToGameDetails);

        adapter.setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                homeViewModel.loadMore(listType, filterParam);
            }
        });
    }

    private void bindData(View root) {
        if (screenTitle == null) {
            //homeViewModel.getRecommendedGameList().observe(getViewLifecycleOwner(), games -> {
            //    ((GameVerticalRVAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
            //});

            homeViewModel.getGamesByPayTypeList().observe(getViewLifecycleOwner(), games -> {
                ((GameVerticalRVAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);

            });
        } else {
            setScreenTitle(root, screenTitle);

            homeViewModel.getGamesByListType(listType).observe(getViewLifecycleOwner(), games -> {
                ((GameVerticalRVAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
                hideLoadingScreen();
            });
            //homeViewModel.getNewGameList().observe(getViewLifecycleOwner(), games -> {
            //    if (listType == HomeViewModel.GameListType.NEW)
            //        ((GameVerticalRVAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
            //});
            //
            //homeViewModel.getHotGameList().observe(getViewLifecycleOwner(), games -> {
            //    if (listType == HomeViewModel.GameListType.HOT)
            //        ((GameVerticalRVAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
            //});
            //
            //homeViewModel.getEditorChoiceList().observe(getViewLifecycleOwner(), games -> {
            //    if (listType == HomeViewModel.GameListType.EDITOR)
            //        ((GameVerticalRVAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
            //});
            //
            //homeViewModel.getTrendingList().observe(getViewLifecycleOwner(), games -> {
            //    if (listType == HomeViewModel.GameListType.TRENDING)
            //        ((GameVerticalRVAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
            //});
            //
            //homeViewModel.getGameByCategoryList().observe(getViewLifecycleOwner(), games -> {
            //    if (listType == HomeViewModel.GameListType.BY_CATEGORY)
            //        ((GameVerticalRVAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
            //});

        }

        updateData();
    }

    private void updateData() {
        homeViewModel.getGamesDataByType(listType, filterParam);
    }

    private void goToGameDetails(String gameId) {
        //Bundle gameIdBundle = new Bundle();
        //gameIdBundle.putString(GameDetailFragment.KEY_GAME_ID, gameId);
        //
        //NavHostFragment.findNavController(this).navigate(R.id.action_gameListFragment_to_gameDetailFragment, gameIdBundle);
        moveTo(GameDetailFragment.newInstance(gameId), GameDetailFragment.class.getSimpleName());
    }
}