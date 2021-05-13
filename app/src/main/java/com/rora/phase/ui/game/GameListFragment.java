package com.rora.phase.ui.game;

import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.ui.adapter.GameVerticalRVAdapter;
import com.rora.phase.ui.viewmodel.HomeViewModel;
import com.rora.phase.utils.callback.ILoadMore;
import com.rora.phase.utils.ui.BaseFragment;

import java.util.Objects;

public class GameListFragment extends BaseFragment {

    private RecyclerView rclvGameList;
    private ImageView errImv;

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
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        if (getArguments() != null) {
            screenTitle = getArguments().getString(SCREEN_TITLE_PARAM);
            filterParam = getArguments().getString(KEY_FILTER_PARAM);
            listType = (HomeViewModel.GameListType) getArguments().getSerializable(LIST_TYPE_PARAM);
        }

        View root = inflater.inflate(R.layout.fragment_game_list, container, false);

        rclvGameList = root.findViewById(R.id.game_list_vertical_rclv);
        errImv = root.findViewById(R.id.error_imv);

        ViewCompat.setOnApplyWindowInsetsListener(root.findViewById(R.id.toolbar), (v, insets) -> {
            root.findViewById(R.id.toolbar).setPadding(0, insets.getSystemWindowInsetTop() + (int)getResources().getDimension(R.dimen.minnn_space), 0, 0);
            return insets;
        });

        initView();
        bindData(root);

        return root;
    }

    private void initView() {
        rclvGameList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL , false));
        rclvGameList.setAdapter(new GameVerticalRVAdapter(rclvGameList));

        ((GameVerticalRVAdapter)rclvGameList.getAdapter()).setOnItemSelectedListener((position, selectedItem) -> moveTo(GameDetailFragment.newInstance((Game) selectedItem), GameDetailFragment.class.getSimpleName(), true));
        ((GameVerticalRVAdapter)rclvGameList.getAdapter()).setLoadMore(() -> homeViewModel.loadMore(listType, filterParam));
    }

    private void bindData(View root) {
        if (screenTitle == null) {
            homeViewModel.getGamesByPayTypeList().observe(getViewLifecycleOwner(), games -> {
                errImv.setVisibility(games != null && games.size() != 0 ? View.GONE : View.VISIBLE);

                ((GameVerticalRVAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
            });
            hideActionbar(root);
        } else {
            showActionbar(root, screenTitle, true);
            setScreenTitle(root, screenTitle);

            homeViewModel.getGamesByListType(listType).observe(getViewLifecycleOwner(), games -> {
                errImv.setVisibility(games != null && games.size() != 0 ? View.GONE : View.VISIBLE);

                ((GameVerticalRVAdapter) Objects.requireNonNull(rclvGameList.getAdapter())).bindData(games);
                hideLoadingScreen();
            });
        }

        updateData();
    }

    private void updateData() {
        homeViewModel.getGamesDataByType(listType, filterParam);
    }

}