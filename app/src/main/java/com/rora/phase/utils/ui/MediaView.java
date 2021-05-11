package com.rora.phase.utils.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.rora.phase.R;
import com.rora.phase.utils.MediaHelper;

public class MediaView extends ConstraintLayout {

    private ImageView imageView;
    private ErrorView errView;
    private VideoView videoView;
    private YouTubePlayerView youTubePlayerView;
    private ProgressBar videoPb;

    private boolean isPlayingVideo = false;
    private int pausedMilliSec = 0;

    public MediaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.item_media, this);

        imageView = findViewById(R.id.image_media_imv);
        videoView = findViewById(R.id.video_media_vv);
        youTubePlayerView = findViewById(R.id.yt_video_media);
        errView = findViewById(R.id.media_error);
        videoPb = findViewById(R.id.video_pb);
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        // Optimize later
        //if (isPlayingVideo) {
        //    if (isVisible) {
        //        //videoView.start();
        //        videoView.seekTo(pausedMilliSec);
        //    }
        //    else {
        //        pausedMilliSec = videoView.getCurrentPosition();
        //        videoView.pause();
        //    }
        //}

        super.onVisibilityAggregated(isVisible);
    }

    public void loadImage(String url) {
        if (url == null || url.isEmpty()) {
            errView.setVisibility(VISIBLE);
            return;
        }

        errView.setVisibility(GONE);
        MediaHelper.loadImage(imageView, url);
    }

    public void loadVideo(Lifecycle lifecycle, String url) {
        imageView.setVisibility(GONE);

        if (url == null || url.isEmpty()) {
            errView.setVisibility(VISIBLE);
            return;
        }

        isPlayingVideo = true;
        if (MediaHelper.isYoutubeUrl(url)) {
            youTubePlayerView.setVisibility(VISIBLE);
            MediaHelper.loadYoutubeVideo(lifecycle, youTubePlayerView, url);
            return;
        }

        videoPb.setVisibility(VISIBLE);
        videoView.setVisibility(VISIBLE);
        MediaHelper.loadVideo(videoView, url, true);
        videoView.setOnPreparedListener(mp -> {
            //if (pausedMilliSec != 0)
            //    mp.seekTo(pausedMilliSec);
            //mp.start();
            videoPb.setVisibility(GONE);
        });
    }

}
