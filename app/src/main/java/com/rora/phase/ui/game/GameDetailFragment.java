package com.rora.phase.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.rora.phase.R;
import com.rora.phase.model.Banner;
import com.rora.phase.model.Game;
import com.rora.phase.model.Screenshot;
import com.rora.phase.model.SupportPlayType;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.ui.adapter.BannerVPAdapter;
import com.rora.phase.ui.adapter.CategoryRVAdapter;
import com.rora.phase.ui.adapter.GameRVAdapter;
import com.rora.phase.ui.adapter.GameVerticalRVAdapter;
import com.rora.phase.ui.adapter.PlatformRVAdapter;
import com.rora.phase.ui.settings.auth.AuthActivity;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.utils.DateTimeHelper;
import com.rora.phase.utils.Dialog;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.services.PlayServicesMessageSender;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.rora.phase.ui.adapter.CategoryRVAdapter.MEDIUM_SIZE;

public class GameDetailFragment extends BaseFragment {

    private LinearLayout frameSeries;
    private ImageView imvBanner, imvBannerErr, imvSimilarGamesErr;
    private RecyclerView rclvPlatform, rclvCategory, rclvScreenshot, rclvSeries, rclvSimilar;
    private TextView tvGameName, tvPayType, tvPayTypeDesc, tvAgeRating, tvRelease, tvDesc;
    private LinearLayout frameFirstPlayType, frameLastPlayType;
    private ImageButton btnFavorite, btnPlay, firstPlayTypeBtn, secondPlayTypeBtn, thirdPlayTypeBtn, lastPlayTypeBtn;
    private VideoView trailerVv;
    private YouTubePlayerView trailerYvv;

    private GameViewModel gameViewModel;
    private Game game;
    private boolean isPlayingThisGame;

    public static final String KEY_GAME = "game";


    //----------------------------------- LIFECYCLE ------------------------------------------

    public static GameDetailFragment newInstance(Game game) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_GAME, game);
        GameDetailFragment fragment = new GameDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        if (getArguments() != null) {
            game = getArguments().getParcelable(KEY_GAME);
        }

        View root = inflater.inflate(R.layout.fragment_game_detail, container, false);

        frameSeries = root.findViewById(R.id.frame_series);

        trailerVv = root.findViewById(R.id.trailer_game_vv);
        trailerYvv = root.findViewById(R.id.trailer_game_yvv);
        imvBanner = root.findViewById(R.id.game_banner_imv);
        imvBannerErr = root.findViewById(R.id.error_banner_imv);
        imvSimilarGamesErr = root.findViewById(R.id.error_similar_imv);

        rclvPlatform = root.findViewById(R.id.platform_rclv);
        rclvCategory = root.findViewById(R.id.category_game_rclv);
        rclvScreenshot = root.findViewById(R.id.screenshot_rclv);
        rclvSeries = root.findViewById(R.id.series_rclv);
        rclvSimilar = root.findViewById(R.id.similar_rclv);

        tvGameName = root.findViewById(R.id.game_name_tv);
        tvPayType = root.findViewById(R.id.pay_type_tv);
        tvPayTypeDesc = root.findViewById(R.id.pay_type_desc_tv);
        tvAgeRating = root.findViewById(R.id.age_rating_tv);
        tvRelease = root.findViewById(R.id.release_tv);
        tvDesc = root.findViewById(R.id.game_desc_tv);

        btnFavorite = root.findViewById(R.id.favorite_btn);
        btnPlay = root.findViewById(R.id.play_btn);

        frameFirstPlayType = root.findViewById(R.id.frame_ic_first_play_type);
        frameLastPlayType = root.findViewById(R.id.frame_ic_last_play_type);

        firstPlayTypeBtn = root.findViewById(R.id.ic_first_play_type_ib);
        secondPlayTypeBtn = root.findViewById(R.id.ic_second_play_type_ib);
        thirdPlayTypeBtn = root.findViewById(R.id.ic_third_play_type_ib);
        lastPlayTypeBtn = root.findViewById(R.id.ic_last_play_type_ib);

        initView(root);
        initData();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Game currentPlayingGame = UserRepository.newInstance(getContext()).getCurrentGame();
        btnPlay.setImageResource(isPlayingThisGame ? android.R.drawable.ic_lock_power_off : R.drawable.ic_play);
    }

    //---------------------------------------------------------------------------------------


    private void initView(View root) {
        ViewHelper.setSizePercentageWithScreen(imvBanner, 0, 0.35);

        setupRecyclerView(rclvPlatform, new PlatformRVAdapter(), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL , false));
        setupRecyclerView(rclvCategory,new CategoryRVAdapter(MEDIUM_SIZE, false), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL , false));
        setupRecyclerView(rclvScreenshot, new BannerVPAdapter(.45), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvSeries, new GameRVAdapter(GameRVAdapter.VIEW_TYPE_LANDSCAPE), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rclvSimilar.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL , false));
        rclvSimilar.setAdapter(new GameVerticalRVAdapter(rclvSimilar));
        ((GameVerticalRVAdapter) rclvSimilar.getAdapter()).setOnItemSelectedListener(selectedItem -> moveTo(GameDetailFragment.newInstance((Game) selectedItem), GameDetailFragment.class.getSimpleName()));

        root.findViewById(R.id.back_btn).setOnClickListener(v -> getActivity().onBackPressed());
        btnPlay.setOnClickListener(v -> {
            if (isPlayingThisGame) {
                playServicesMsgSenderCallback.sendMessage(PlayServicesMessageSender.MsgCode.STOP);
                return;
            }

            if (gameViewModel.getCurrentGame().getValue() != null && gameViewModel.getCurrentGame().getValue().getId() != game.getId()) {
                Dialog.displayDialog(getActivity(), "Switch Game", "You are playing " + gameViewModel.getCurrentGame().getValue().getName() + ", do you want to switch to " + game.getName() + " ?", null, null, () -> {
                    try {
                        playServicesMsgSenderCallback.sendMessage(PlayServicesMessageSender.MsgCode.STOP);
                        Thread.sleep(500);

                        startConnect();
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }, () -> {});

                return;
            }

            startConnect();
        });

        btnFavorite.setOnClickListener(v -> {
            //gameViewModel.updateFavorite();
            btnFavorite.setImageResource(R.drawable.ic_favorite);
        });
    }

    private void initData() {
        gameViewModel.getGameData().observe(getViewLifecycleOwner(), this::bindData);
        gameViewModel.getCurrentGame().observe(getViewLifecycleOwner(), game -> {
            isPlayingThisGame = game != null;

            btnPlay.setImageResource(isPlayingThisGame ? android.R.drawable.ic_lock_power_off : R.drawable.ic_play);
        });

        //gameViewModel.getNewGameList().observe(getViewLifecycleOwner(), gameList -> {
        gameViewModel.getSimilarGames().observe(getViewLifecycleOwner(), gameList -> {
            if (gameList != null && gameList.size() != 0) {
                imvSimilarGamesErr.setVisibility(GONE);
                ((GameVerticalRVAdapter)rclvSimilar.getAdapter()).bindData(gameList);
            } else {
                imvSimilarGamesErr.setVisibility(View.VISIBLE);
            }
        });

        if (game != null) {
            gameViewModel.getGame(game.getId().toString());
            gameViewModel.getSimilarGameList(game.getId().toString());
            //gameViewModel.getNewGameListData(1, 5);

            imvBannerErr.setVisibility(GONE);
            trailerVv.setVisibility(View.VISIBLE);
            trailerYvv.setVisibility(View.VISIBLE);
        } else {
            imvBannerErr.setVisibility(View.VISIBLE);
            imvBanner.setVisibility(View.INVISIBLE);
            trailerVv.setVisibility(GONE);
            trailerYvv.setVisibility(GONE);
        }
        //if (game != null)
        //    bindData(game);
    }

    private void bindData(Game game) {
        if (game == null || this.game.getId() != game.getId()) //Handle stupid err - duplicate response from live data
            return;

        this.game = game;
        game.setTrailer("https://steamcdn-a.akamaihd.net/steam/apps/256679772/movie_max.mp4");
        //game.setTrailer("https://youtu.be/h7Xs642lALk");
        if (game.getTrailer() != null && !game.getTrailer().isEmpty()) {
            if (MediaHelper.isYoutubeUrl(game.getTrailer())) {
                trailerYvv.setVisibility(View.VISIBLE);
                MediaHelper.loadYoutubeVideo(getLifecycle(), trailerYvv, game.getTrailer());
            }
            else {
                trailerVv.setVisibility(View.VISIBLE);
                MediaHelper.loadVideo(trailerVv, game.getTrailer(), true);
            }
        }

        MediaHelper.loadImage(imvBanner, game.getBanner());

        tvGameName.setText(game.getName());
        tvPayType.setText(game.getPayTypeName());
        tvPayTypeDesc.setText(game.getPayTypeDesc());
        tvAgeRating.setText(game.getPegiAge().toString() + "+");
        tvRelease.setText(DateTimeHelper.format(game.getReleaseDate()));
        tvDesc.setText(Html.fromHtml(game.getDesc(), Html.FROM_HTML_MODE_COMPACT));

        ((PlatformRVAdapter)rclvPlatform.getAdapter()).bindData(game.getPlatforms());
        ((CategoryRVAdapter)rclvCategory.getAdapter()).bindData(game.getTags());

        List<Banner> banners = new ArrayList<>();
        banners.add(new Banner(game.getBanner()));
        for (Screenshot screenshot : game.getScreenshots()) {
            banners.add(new Banner(screenshot.getLink()));
        }
        ((BannerVPAdapter)rclvScreenshot.getAdapter()).bindData(banners);

        List<SupportPlayType> playTypeList = game.getSupportPlayTypes();
        if (playTypeList != null && playTypeList.size() != 0) {
            int numberOfPlayType = game.getSupportPlayTypes().size();
            switch (numberOfPlayType) {
                case 1:
                    MediaHelper.loadSvg(firstPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(0).getId()));
                    firstPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(0).getPlayType(), Toast.LENGTH_SHORT).show());
                    secondPlayTypeBtn.setVisibility(GONE);
                    thirdPlayTypeBtn.setVisibility(GONE);
                    frameLastPlayType.setVisibility(GONE);
                    break;
                case 2:
                    MediaHelper.loadSvg(secondPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(0).getId()));
                    secondPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(0).getPlayType(), Toast.LENGTH_SHORT).show());
                    MediaHelper.loadSvg(thirdPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(1).getId()));
                    thirdPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(1).getPlayType(), Toast.LENGTH_SHORT).show());
                    frameFirstPlayType.setVisibility(GONE);
                    frameLastPlayType.setVisibility(GONE);
                    break;
                case 3:
                    MediaHelper.loadSvg(firstPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(0).getId()));
                    firstPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(0).getPlayType(), Toast.LENGTH_SHORT).show());
                    MediaHelper.loadSvg(secondPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(1).getId()));
                    secondPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(1).getPlayType(), Toast.LENGTH_SHORT).show());
                    MediaHelper.loadSvg(lastPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(2).getId()));
                    lastPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(2).getPlayType(), Toast.LENGTH_SHORT).show());

                    thirdPlayTypeBtn.setVisibility(GONE);
                    break;
                case 4:
                    MediaHelper.loadSvg(firstPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(0).getId()));
                    firstPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(0).getPlayType(), Toast.LENGTH_SHORT).show());
                    MediaHelper.loadSvg(secondPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(1).getId()));
                    secondPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(1).getPlayType(), Toast.LENGTH_SHORT).show());
                    MediaHelper.loadSvg(lastPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(2).getId()));
                    lastPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(2).getPlayType(), Toast.LENGTH_SHORT).show());
                    MediaHelper.loadSvg(lastPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(3).getId()));
                    lastPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(3).getPlayType(), Toast.LENGTH_SHORT).show());
                    break;
                default:
                    MediaHelper.loadSvg(firstPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(0).getId()));
                    firstPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(0).getPlayType(), Toast.LENGTH_SHORT).show());
                    MediaHelper.loadSvg(secondPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(1).getId()));
                    secondPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(1).getPlayType(), Toast.LENGTH_SHORT).show());
                    MediaHelper.loadSvg(thirdPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(2).getId()));
                    thirdPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(2).getPlayType(), Toast.LENGTH_SHORT).show());
                    MediaHelper.loadSvg(lastPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(4).getId()));
                    lastPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(4).getPlayType(), Toast.LENGTH_SHORT).show());
                    break;
            }
        } else {
            frameFirstPlayType.setVisibility(GONE);
            secondPlayTypeBtn.setVisibility(GONE);
            thirdPlayTypeBtn.setVisibility(GONE);
            frameLastPlayType.setVisibility(GONE);
        }

        //if (game.getSeriesId() == 0) {
            frameSeries.setVisibility(GONE);
        //} else {
        //    ((GameMinInfoRecyclerViewAdapter)rclvSeries.getAdapter()).bindData(game.getSeriesId());
        //}
        //((GameVerticalRVAdapter)rclvSimilar.getAdapter()).bindData(game.getPlatforms());

        hideLoadingScreen();
    }

    private void setupRecyclerView(RecyclerView view, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        view.setAdapter(adapter);
        view.setLayoutManager(layoutManager);
        view.setHasFixedSize(true);
    }

    public int getCurrentGameId() {
        return game.getId();
    }

    private void startConnect() {
        if (!gameViewModel.isUserLogged()) {
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.putExtra(AuthActivity.START_IN_APP_PARAM, true);
            startActivity(intent);
            return;
        }

        Intent playIntent = new Intent(getContext(), LoadingGameActivity.class);
        playIntent.putExtra(KEY_GAME, game.toJson());
        getActivity().startActivity(playIntent);
    }

}