package com.rora.phase.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.MediaImage;
import com.rora.phase.model.Screenshot;
import com.rora.phase.model.SupportPlayType;
import com.rora.phase.model.ui.Media;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.ui.adapter.CategoryRVAdapter;
import com.rora.phase.ui.adapter.GameRVAdapter;
import com.rora.phase.ui.adapter.GameVerticalRVAdapter;
import com.rora.phase.ui.adapter.MediaAdapter;
import com.rora.phase.ui.adapter.PlatformRVAdapter;
import com.rora.phase.ui.auth.AuthActivity;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.DateTimeHelper;
import com.rora.phase.utils.Dialog;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.services.PlayServicesMessageSender;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.CustomToolbar;
import com.rora.phase.utils.ui.ExpandableTextView;
import com.rora.phase.utils.ui.MediaView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.rora.phase.ui.adapter.CategoryRVAdapter.AUTO_SIZE;
import static com.rora.phase.ui.adapter.CategoryRVAdapter.NONE_SELECT;
import static com.rora.phase.ui.game.MediaViewerActivity.MEDIA_LIST_PARAM;
import static com.rora.phase.ui.game.MediaViewerActivity.POSITION_PARAM;
import static com.rora.phase.ui.game.MediaViewerActivity.SCREEN_TITLE_PARAM;

public class GameDetailFragment extends BaseFragment {

    private NestedScrollView scrollView;
    private LinearLayout frameSeries, frameSimilar;
    private FrameLayout frameVideos;
    private MediaView topMediaView, mediaContainerMv;
    private ImageView imvSimilarGamesErr, bannerTile;
    private RecyclerView rclvPlatform, rclvCategory, rclvVideos, rclvScreenshot, rclvSeries, rclvSimilar;
    private TextView tvGameName, tvPayType, tvPayTypeDesc, tvAgeRating, tvRelease;
    private ExpandableTextView tvDesc;
    private CustomToolbar toolbar;
    private ImageButton btnFavorite, firstPlayTypeBtn, secondPlayTypeBtn, thirdPlayTypeBtn, lastPlayTypeBtn;
    private Button btnPlay;
    private ImageView btnPlayImv;

    private GameViewModel gameViewModel;
    private UserViewModel userViewModel;
    private Game game;
    private boolean isPlayingThisGame;
    private boolean isFirstInit = true;

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
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        if (getArguments() != null) {
            game = getArguments().getParcelable(KEY_GAME);
        }

        View root = inflater.inflate(R.layout.fragment_game_detail, container, false);

        scrollView = root.findViewById(R.id.game_details_scroll_view);
        frameSeries = root.findViewById(R.id.frame_series);
        frameSimilar = root.findViewById(R.id.similar_games_frame);
        frameVideos = root.findViewById(R.id.frame_videos_game_details);

        topMediaView = root.findViewById(R.id.top_media_game_details);
        imvSimilarGamesErr = root.findViewById(R.id.error_similar_imv);
        bannerTile = root.findViewById(R.id.banner_tile_imv);
        mediaContainerMv = root.findViewById(R.id.media_container_game_details);

        rclvPlatform = root.findViewById(R.id.platform_rclv);
        rclvCategory = root.findViewById(R.id.category_game_rclv);
        rclvVideos = root.findViewById(R.id.video_rclv);
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
        btnPlayImv = root.findViewById(R.id.play_btn_imv);
        toolbar = root.findViewById(R.id.toolbar_game_details);

        firstPlayTypeBtn = root.findViewById(R.id.ic_first_play_type_ib);
        secondPlayTypeBtn = root.findViewById(R.id.ic_second_play_type_ib);
        thirdPlayTypeBtn = root.findViewById(R.id.ic_third_play_type_ib);
        lastPlayTypeBtn = root.findViewById(R.id.ic_last_play_type_ib);

        initView();
        initData();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaContainerMv.initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();

        Game currentPlayingGame = UserRepository.newInstance(getContext()).getCurrentGame();
        MediaHelper.loadSvg(btnPlayImv, isPlayingThisGame ? android.R.drawable.ic_lock_power_off : R.drawable.ic_play);
        btnPlay.setText(isPlayingThisGame ? R.string.stop_text : R.string.play_title);
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaContainerMv.releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gameViewModel.resetGameData();
    }

    //---------------------------------------------------------------------------------------


    private void initView() {
        setupRecyclerView(rclvPlatform, new PlatformRVAdapter(), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvCategory, new CategoryRVAdapter(AUTO_SIZE, false, NONE_SELECT), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvVideos, new MediaAdapter(false, 0.65, true), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvScreenshot, new MediaAdapter(true, 0.65, true), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvSeries, new GameRVAdapter(GameRVAdapter.VIEW_TYPE_LANDSCAPE), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rclvSimilar.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rclvSimilar.setAdapter(new GameVerticalRVAdapter(rclvSimilar));
        ((GameVerticalRVAdapter) rclvSimilar.getAdapter()).setOnItemSelectedListener((position, selectedItem) -> moveTo(GameDetailFragment.newInstance((Game) selectedItem), GameDetailFragment.class.getSimpleName(), true));

        btnPlay.setOnClickListener(v -> {
            if (isPlayingThisGame) {
                playServicesMsgSenderCallback.sendMessage(PlayServicesMessageSender.MsgCode.STOP);
                return;
            }

            if (gameViewModel.getCurrentPlayingGame().getValue() != null && gameViewModel.getCurrentPlayingGame().getValue().getId() != game.getId()) {
                Dialog.displayDialog(getActivity(), "Switch Game", getString(R.string.are_playing_msg) + " " + gameViewModel.getCurrentPlayingGame().getValue().getName() + ", " + getString(R.string.switch_game_msg) + " " + game.getName() + " ?", null, null, () -> {
                    try {
                        playServicesMsgSenderCallback.sendMessage(PlayServicesMessageSender.MsgCode.SWITCH);
                        Thread.sleep(500);

                        startConnect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, () -> {
                });
                return;
            }

            Dialog.displayDialog(getActivity(), getString(R.string.start_playing_msg) + " " + game.getName(), null, null, null, this::startConnect, () -> { });
        });

        btnFavorite.setOnClickListener(v -> {
            showLoadingScreen();
            userViewModel.updateFavorite(game, (err, data) -> {
                if (err != null && !err.isEmpty()) {
                    if (!err.equals("401"))
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show());
                }
                else
                    btnFavorite.setImageResource(game.getFavorited() ? R.drawable.ic_favorite : R.drawable.ic_unfavorite);

                hideLoadingScreen();
            });
        });
    }

    private void initData() {
        gameViewModel.getGameData().observe(getViewLifecycleOwner(), this::bindData);
        gameViewModel.getCurrentPlayingGame().observe(getViewLifecycleOwner(), game -> {
            isPlayingThisGame = game != null && game.getId() == this.game.getId();

            MediaHelper.loadSvg(btnPlayImv, isPlayingThisGame ? android.R.drawable.ic_lock_power_off : R.drawable.ic_play);
            btnPlay.setText(isPlayingThisGame ? R.string.stop_text : R.string.play_title);
        });

        gameViewModel.getSimilarGames().observe(getViewLifecycleOwner(), gameList -> {
            if (gameList != null && gameList.size() != 0) {
                frameSimilar.setVisibility(View.VISIBLE);
                imvSimilarGamesErr.setVisibility(GONE);
                ((GameVerticalRVAdapter) rclvSimilar.getAdapter()).bindData(gameList);
            } else {
                frameSimilar.setVisibility(GONE);
                imvSimilarGamesErr.setVisibility(View.VISIBLE);
            }
        });

        if (game != null && game.getId() != null) {
            toolbar.showActionbar(((AppCompatActivity)getActivity()),"", true, v -> getActivity().onBackPressed());
            gameViewModel.getGame(game.getId().toString());
            gameViewModel.getSimilarGameList(game.getId().toString());
        }
        //if (game != null)
        //    bindData(game);
    }

    private void bindData(Game game) {
        if (game == null || this.game.getId() != game.getId()) {
            //Handle stupid err - duplicate response from live data
            if (!isFirstInit) {
                hideLoadingScreen();
            }
            isFirstInit = false;
            return;
        }

        this.game = game;

        MediaHelper.loadImage(bannerTile, game.getTile());
        tvGameName.setText(game.getName());
        tvPayType.setText(game.getPayTypeName());
        tvPayTypeDesc.setText(game.getPayTypeDesc());
        tvAgeRating.setText(game.getPegiAge().toString() + "+");
        tvRelease.setText(DateTimeHelper.format(game.getReleaseDate()));
        tvDesc.setText(Html.fromHtml(game.getDesc(), Html.FROM_HTML_MODE_COMPACT));
        btnFavorite.setImageResource(game.getFavorited() ? R.drawable.ic_favorite : R.drawable.ic_unfavorite);

        ((PlatformRVAdapter) rclvPlatform.getAdapter()).bindData(game.getPlatforms());
        ((CategoryRVAdapter) rclvCategory.getAdapter()).bindData(game.getTags(), null);

        //Setup media

        ArrayList<MediaImage> screenshots = new ArrayList<>();
        for (Screenshot screenshot : game.getScreenshots()) {
            screenshots.add(new MediaImage(screenshot));
        }

        topMediaView.loadImage(game.getBanner().getAvailableLink(Media.Quality.MEDIUM));
        if (game.getVideos().size() == 0) {
            frameVideos.setVisibility(GONE);
            mediaContainerMv.loadImage(screenshots.size() != 0 ? screenshots.get(0).getAvailableLink(Media.Quality.MEDIUM) : null);
        } else {
            //topMediaView.loadVideo(getLifecycle(), game.getVideos().get(0).getAvailableLink(Media.Quality.MEDIUM), false, false, true, true);
            mediaContainerMv.loadVideo(getLifecycle(), game.getVideos().get(0).getAvailableLink(Media.Quality.MEDIUM), MediaView.LIGHT_VIDEO_MODE);
            if (game.getVideos().size() > 0) {
                frameVideos.setVisibility(View.VISIBLE);

                List<Media> videos = new ArrayList<>(game.getVideos());
                videos.remove(0);
                ((MediaAdapter) rclvVideos.getAdapter()).bindData(videos);

                //((MediaAdapter) rclvVideos.getAdapter()).setOnItemSelectedListener((position, selectedItem) -> {
                //    Intent mediaIntent = new Intent(getActivity(), MediaViewerActivity.class);
                //
                //    Bundle mediaBundle = new Bundle();
                //    mediaBundle.putString(SCREEN_TITLE_PARAM, getResources().getString(R.string.screenshot_title));
                //    mediaBundle.putSerializable(MEDIA_LIST_PARAM, (Serializable) game.getVideos());
                //    mediaBundle.putInt(POSITION_PARAM, position);
                //    mediaIntent.putExtras(mediaBundle);
                //
                //    startActivity(mediaIntent);
                //});
            }

        }

        ((MediaAdapter) rclvScreenshot.getAdapter()).bindData(screenshots);
        ((MediaAdapter) rclvScreenshot.getAdapter()).setOnItemSelectedListener((position, selectedItem) -> {
            Intent mediaIntent = new Intent(getActivity(), MediaViewerActivity.class);

            Bundle mediaBundle = new Bundle();
            mediaBundle.putString(SCREEN_TITLE_PARAM, getResources().getString(R.string.screenshot_title));
            mediaBundle.putSerializable(MEDIA_LIST_PARAM, screenshots);
            mediaBundle.putInt(POSITION_PARAM, position);
            mediaIntent.putExtras(mediaBundle);

            startActivity(mediaIntent);
        });

        List<SupportPlayType> playTypeList = game.getSupportPlayTypes();
        if (playTypeList != null && playTypeList.size() != 0) {
            int numberOfPlayType = game.getSupportPlayTypes().size();
            switch (numberOfPlayType) {
                case 1:
                    //MediaHelper.loadSvg(firstPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(0).getId()));
                    firstPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(0).getPlayType(), Toast.LENGTH_SHORT).show());
                    secondPlayTypeBtn.setVisibility(GONE);
                    thirdPlayTypeBtn.setVisibility(GONE);
                    lastPlayTypeBtn.setVisibility(GONE);
                    break;
                case 2:
                    //MediaHelper.loadSvg(secondPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(0).getId()));
                    secondPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(0).getPlayType(), Toast.LENGTH_SHORT).show());
                    //MediaHelper.loadSvg(thirdPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(1).getId()));
                    thirdPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(1).getPlayType(), Toast.LENGTH_SHORT).show());
                    firstPlayTypeBtn.setVisibility(GONE);
                    lastPlayTypeBtn.setVisibility(GONE);
                    break;
                case 3:
                    //MediaHelper.loadSvg(firstPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(0).getId()));
                    firstPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(0).getPlayType(), Toast.LENGTH_SHORT).show());
                    //MediaHelper.loadSvg(secondPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(1).getId()));
                    secondPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(1).getPlayType(), Toast.LENGTH_SHORT).show());
                    //MediaHelper.loadSvg(lastPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(2).getId()));
                    lastPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(2).getPlayType(), Toast.LENGTH_SHORT).show());
                    thirdPlayTypeBtn.setVisibility(GONE);
                    break;
                case 4:
                    //MediaHelper.loadSvg(firstPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(0).getId()));
                    firstPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(0).getPlayType(), Toast.LENGTH_SHORT).show());
                    //MediaHelper.loadSvg(secondPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(1).getId()));
                    secondPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(1).getPlayType(), Toast.LENGTH_SHORT).show());
                    //MediaHelper.loadSvg(lastPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(2).getId()));
                    lastPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(2).getPlayType(), Toast.LENGTH_SHORT).show());
                    //MediaHelper.loadSvg(lastPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(3).getId()));
                    lastPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(3).getPlayType(), Toast.LENGTH_SHORT).show());
                    break;
                default:
                    //MediaHelper.loadSvg(firstPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(0).getId()));
                    firstPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(0).getPlayType(), Toast.LENGTH_SHORT).show());
                    //MediaHelper.loadSvg(secondPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(1).getId()));
                    secondPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(1).getPlayType(), Toast.LENGTH_SHORT).show());
                    //MediaHelper.loadSvg(thirdPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(2).getId()));
                    thirdPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(2).getPlayType(), Toast.LENGTH_SHORT).show());
                    //MediaHelper.loadSvg(lastPlayTypeBtn, SupportPlayType.Type.getIconForType(playTypeList.get(4).getId()));
                    lastPlayTypeBtn.setOnClickListener(v -> Toast.makeText(getContext(), playTypeList.get(4).getPlayType(), Toast.LENGTH_SHORT).show());
                    break;
            }
        } else {
            firstPlayTypeBtn.setVisibility(GONE);
            secondPlayTypeBtn.setVisibility(GONE);
            thirdPlayTypeBtn.setVisibility(GONE);
            lastPlayTypeBtn.setVisibility(GONE);
        }

        //if (game.getSeriesId() == 0) {
        frameSeries.setVisibility(GONE);
        //} else {
        //    ((GameMinInfoRecyclerViewAdapter)rclvSeries.getAdapter()).bindData(game.getSeriesId());
        //}
        //((GameVerticalRVAdapter)rclvSimilar.getAdapter()).bindData(game.getPlatforms());


        scrollView.scrollTo(0,0);
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