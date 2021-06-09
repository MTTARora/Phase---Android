package com.rora.phase.utils.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.lifecycle.Lifecycle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.rora.phase.R;
import com.rora.phase.utils.MediaHelper;

import carbon.widget.ImageView;

public class MediaView extends ConstraintLayout {

    private carbon.widget.ConstraintLayout frame;
    private ImageView imageView;
    //private ErrorView errView;
    private VideoView videoView;
    private YouTubePlayerView youTubePlayerView;
    private ContentLoadingProgressBar videoPb;
    private MediaController mediaController;

    private boolean isPlayingVideo = false;
    private boolean isPaused = false;
    private int pausedMilliSec = 0;

    public MediaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_media, this);

        frame = findViewById(R.id.frame_media_view);
        imageView = findViewById(R.id.image_media_imv);
        videoView = findViewById(R.id.video_media_vv);
        youTubePlayerView = findViewById(R.id.yt_video_media);
        //errView = findViewById(R.id.media_error);
        videoPb = findViewById(R.id.video_pb);
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        // Optimize later
        if (isPlayingVideo) {
            if (isVisible) {
                isPaused = true;
                //videoPb.setVisibility(VISIBLE);
                //videoView.start();
                //videoView.seekTo(pausedMilliSec);
            }
            //else {
            //    pausedMilliSec = videoView.getCurrentPosition();
            //    videoView.pause();
            //}
        }

        super.onVisibilityAggregated(isVisible);
    }

    public void setCornerRadius(float radius) {
        frame.setCornerRadius(radius);
    }

    public void loadImage(String url) {
        if (url == null || url.isEmpty())
            return;

        MediaHelper.loadImage(imageView, url);
    }

    public void loadVideo(Lifecycle lifecycle, String url, boolean enableControls, boolean showLoading, boolean enableRepeat, boolean autoPlay) {
        imageView.setVisibility(GONE);
        videoPb.setVisibility(showLoading ? VISIBLE : GONE);

        if (url == null || url.isEmpty())
            return;

        isPlayingVideo = true;
        if (MediaHelper.isYoutubeUrl(url)) {
            youTubePlayerView.setVisibility(VISIBLE);
            MediaHelper.loadYoutubeVideo(lifecycle, youTubePlayerView, url);
            videoPb.setVisibility(GONE);
            return;
        }

        if (enableControls) {
            if (mediaController == null) {
                mediaController = new MediaController(getContext());
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);
            }
        }

        videoView.setVisibility(VISIBLE);
        MediaHelper.loadVideo(videoView, url, enableRepeat, autoPlay);

        videoView.setOnPreparedListener(mp -> {
            //if (isPaused) {
            //    mp.seekTo(pausedMilliSec);
            //    mp.start();
            //    isPaused = !isPaused;
            //}
            if (!autoPlay)
                mp.seekTo(5000);
            videoPb.setVisibility(GONE);
        });
    }

}
