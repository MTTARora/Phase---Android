package com.rora.phase.utils.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.lifecycle.Lifecycle;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.rora.phase.R;
import com.rora.phase.utils.MediaHelper;

import carbon.widget.ImageView;

public class MediaView extends ConstraintLayout implements Player.Listener {

    private carbon.widget.ConstraintLayout frame;
    private ImageView imageView;

    private FrameLayout frameVideo;
    private PlayerView videoView;
    private YouTubePlayerView youTubePlayerView;

    private ContentLoadingProgressBar videoPb;

    private SimpleExoPlayer mediaController;
    private String mediaUri = "";
    private int mode = LIGHT_VIDEO_MODE;
    private boolean enableControls = true;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private boolean isPlayingVideo = false;
    private boolean isFirstTimePlaying = true;

    public static final int LIGHT_VIDEO_MODE = 0;
    public static final int NORMAL_VIDEO_MODE = 1;

    public MediaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_media, this);

        frame = findViewById(R.id.frame_media_view);
        imageView = findViewById(R.id.image_media_imv);

        frameVideo = findViewById(R.id.frame_video_media_view);
        videoView = findViewById(R.id.player_view);
        youTubePlayerView = findViewById(R.id.yt_video_media);

        videoPb = findViewById(R.id.video_pb);
    }

    //@Override
    //public void onVisibilityAggregated(boolean isVisible) {
    //    if (isPlayingVideo) {
    //        if (isVisible)
    //            initializePlayer();
    //        else
    //            releasePlayer();
    //    }
    //
    //    super.onVisibilityAggregated(isVisible);
    //}

    public void setCornerRadius(float radius) {
        frame.setCornerRadius(radius);
    }

    public void loadImage(String url) {
        frameVideo.setVisibility(GONE);
        if (url == null || url.isEmpty())
            return;

        MediaHelper.loadImage(imageView, url);
    }

    public void loadVideo(Lifecycle lifecycle, String url, int mode) {
        this.mode = mode;
        if (mode == LIGHT_VIDEO_MODE)
            loadVideo(lifecycle, url, true, false, false, false);
        else
            loadVideo(lifecycle, url, true, true, false, false);
    }

    public void loadVideoOptions(Lifecycle lifecycle, String url, boolean enableControls, boolean showLoading, boolean enableRepeat, boolean autoPlay) {
        loadVideo(lifecycle, url, enableControls, showLoading, enableRepeat, autoPlay);
    }

    private void loadVideo(Lifecycle lifecycle, String url, boolean enableControls, boolean showLoading, boolean enableRepeat, boolean autoPlay) {
        imageView.setVisibility(GONE);
        videoPb.setVisibility(showLoading ? VISIBLE : GONE);
        this.enableControls = enableControls;

        if (url == null || url.isEmpty())
            return;

        isPlayingVideo = true;
        frameVideo.setVisibility(VISIBLE);

        if (MediaHelper.isYoutubeUrl(url)) {
            youTubePlayerView.setVisibility(VISIBLE);
            MediaHelper.loadYoutubeVideo(lifecycle, youTubePlayerView, url);
            videoPb.setVisibility(GONE);
            return;
        }

        videoView.setUseController(enableControls);
        if (enableControls) {
            if (mode == LIGHT_VIDEO_MODE) {
                videoView.setControllerHideOnTouch(false);
                videoView.setControllerAutoShow(true);
            }
            else
                videoView.setControllerShowTimeoutMs(3000);
        }

        videoView.setVisibility(VISIBLE);
        this.mediaUri = url;
        this.playWhenReady = autoPlay;
        this.currentWindow = 0;
        this.playbackPosition = 5000;

        findViewById(R.id.frame_info_media_controls).setVisibility(GONE);
        initializePlayer();
    }

    public void initializePlayer() {
        if (!isPlayingVideo)
            return;

        if (mediaController == null) {
            mediaController = new SimpleExoPlayer.Builder(getContext()).build();
            mediaController.addListener(this);
            videoView.setPlayer(mediaController);
        }

        if (mediaController.getCurrentMediaItem() == null) {
            MediaItem mediaItem = MediaItem.fromUri(mediaUri);
            mediaController.setMediaItem(mediaItem);
        }

        mediaController.setPlayWhenReady(playWhenReady);
        mediaController.seekTo(currentWindow, playbackPosition);
        //mediaController.prepare();
    }

    public void releasePlayer() {
        if (mediaController != null) {
            playWhenReady = mediaController.getPlayWhenReady();
            playbackPosition = mediaController.getCurrentPosition();
            currentWindow = mediaController.getCurrentWindowIndex();
            mediaController.removeListener(this);
            mediaController.release();
            mediaController = null;
        }
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if (isPlaying) {
            if (isFirstTimePlaying) {
                if (enableControls) {
                    //videoView.setControllerShowTimeoutMs(3000);
                    mediaController.seekTo(0, 0);
                }
                isFirstTimePlaying = false;
            }
            findViewById(R.id.frame_info_media_controls).setVisibility(VISIBLE);
            videoView.setControllerAutoShow(false);
            videoView.setControllerHideOnTouch(true);
        } else {
            // Not playing because playback is paused, ended, suppressed, or the player
            // is buffering, stopped or failed. Check player.getPlayWhenReady,
            // player.getPlaybackState, player.getPlaybackSuppressionReason and
            // player.getPlaybackError for details.
            if (enableControls) {
                findViewById(R.id.frame_info_media_controls).setVisibility(GONE);
                videoView.setControllerAutoShow(true);
                videoView.setControllerHideOnTouch(false);
                videoView.showController();
            }
        }
    }

    @Override
    public void onPlaybackStateChanged(int state) {
    }
}
