package com.rora.phase.ui.game;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rora.phase.R;
import com.rora.phase.model.Banner;
import com.rora.phase.model.Game;
import com.rora.phase.model.Screenshot;
import com.rora.phase.ui.adapter.BannerVPAdapter;
import com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter;
import com.rora.phase.ui.adapter.GameMinInfoRecyclerViewAdapter;
import com.rora.phase.ui.adapter.GameVerticalRVAdapter;
import com.rora.phase.ui.adapter.PlatformRecyclerViewAdapter;
import com.rora.phase.ui.settings.auth.SignInActivity;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.utils.DateTimeHelper;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.MIN_SIZE;

public class GameDetailFragment extends BaseFragment {

    private ImageView imvBanner;
    private RecyclerView rclvPlatform, rclvCategory, rclvScreenshot, rclvSeries, rclvSimilar;
    private TextView tvGameName, tvPayType, tvAgeRating, tvRelease, tvDesc;
    private ImageButton btnFavorite;

    private GameViewModel gameViewModel;
    private String gameId;

    public static final String KEY_GAME_ID = "gameId";

    public static GameDetailFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        args.putString(KEY_GAME_ID, gameId);
        GameDetailFragment fragment = new GameDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }


    //----------------------------------- LIFECYCLE ------------------------------------------

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        if (getArguments() != null) {
            gameId = getArguments().getString(KEY_GAME_ID);
        }

        View root = inflater.inflate(R.layout.fragment_game_detail, container, false);
        imvBanner = root.findViewById(R.id.game_banner_imv);

        rclvPlatform = root.findViewById(R.id.platform_rclv);
        rclvCategory = root.findViewById(R.id.category_game_rclv);
        rclvScreenshot = root.findViewById(R.id.screenshot_rclv);
        rclvSeries = root.findViewById(R.id.series_rclv);
        rclvSimilar = root.findViewById(R.id.similar_rclv);

        tvGameName = root.findViewById(R.id.game_name_tv);
        tvPayType = root.findViewById(R.id.pay_type_tv);
        tvAgeRating = root.findViewById(R.id.age_rating_tv);
        tvRelease = root.findViewById(R.id.release_tv);
        tvDesc = root.findViewById(R.id.game_desc_tv);

        btnFavorite = root.findViewById(R.id.favorite_btn);

        initView(root);
        initData();
        return root;
    }

    //---------------------------------------------------------------------------------------


    private void initView(View root) {
        ViewHelper.setSizePercentageWithScreen(imvBanner, 0, 0.35);

        setupRecyclerView(rclvPlatform, new PlatformRecyclerViewAdapter(), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL , false));
        setupRecyclerView(rclvCategory,new CategoryRecyclerViewAdapter( 0.11, MIN_SIZE, false, null), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL , false));
        setupRecyclerView(rclvScreenshot, new BannerVPAdapter(), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvSeries, new GameMinInfoRecyclerViewAdapter(GameMinInfoRecyclerViewAdapter.VIEW_TYPE_EXPANDED, 0.8), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rclvSimilar.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL , false));
        GameVerticalRVAdapter adapter = new GameVerticalRVAdapter(rclvSimilar);
        rclvSimilar.setAdapter(adapter);

        root.findViewById(R.id.back_btn).setOnClickListener(v -> getActivity().onBackPressed());
        root.findViewById(R.id.play_btn).setOnClickListener(v -> {
            if (!gameViewModel.isUserLogged()) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.putExtra(SignInActivity.START_IN_APP_PARAM, true);
                startActivity(intent);
                return;
            }

            getActivity().startActivity(new Intent(getContext(), LoadingGameActivity.class));
        });

        btnFavorite.setOnClickListener(v -> {
            //gameViewModel.updateFavorite();
            btnFavorite.setImageResource(R.drawable.ic_favorite);
        });
    }

    private void initData() {
        gameViewModel.getGameData().observe(getViewLifecycleOwner(), this::bindData);

        //STEP 1: Get computer details from server
        //gameViewModel.getComputerDetails().observe(getViewLifecycleOwner(), computerDetails -> {
        //    if (computerDetails == null) {
        //        Dialog.displayDialog(getActivity(), getResources().getString(R.string.err), getResources().getString(R.string.undetected_error), false);
        //        return;
        //    }
        //    //STEP 2: Pass computer data to loading screen
        //    Bundle bundle = new Bundle();
        //    bundle.putSerializable(LoadingGameActivity.COMPUTER_PARAM, computerDetails);
        //    Intent intent = new Intent(getContext(), LoadingGameActivity.class);
        //    intent.putExtra("LoadingGameActivityBundle", bundle);
        //});

        gameViewModel.getGame(gameId);
    }

    private void bindData(Game game) {
        MediaHelper.loadImage(game.getBanner(), imvBanner);

        tvGameName.setText(game.getName());
        tvPayType.setText(game.getPayTypeName());
        tvAgeRating.setText(getResources().getString(R.string.age_rating_title) + " " + game.getPegiAge().toString() + "+");
        tvRelease.setText(getResources().getString(R.string.release_title) + " " + DateTimeHelper.format(game.getReleaseDate()));
        tvDesc.setText(game.getDesc());

        ((PlatformRecyclerViewAdapter)rclvPlatform.getAdapter()).bindData(game.getPlatforms());
        ((CategoryRecyclerViewAdapter)rclvCategory.getAdapter()).bindData(game.getTags());

        List<Banner> banners = new ArrayList<>();
        banners.add(new Banner(game.getBanner()));
        for (Screenshot screenshot : game.getScreenshots()) {
            banners.add(new Banner(screenshot.getLink()));
        }
        ((BannerVPAdapter)rclvScreenshot.getAdapter()).bindData(banners);
        //((GameMinInfoRecyclerViewAdapter)rclvSeries.getAdapter()).bindData(game.getG);
        //((GameVerticalRVAdapter)rclvSimilar.getAdapter()).bindData(game.getPlatforms());

        hideLoadingScreen();
    }

    private void setupRecyclerView(RecyclerView view, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        view.setAdapter(adapter);
        view.setLayoutManager(layoutManager);
        view.setHasFixedSize(true);
    }

}