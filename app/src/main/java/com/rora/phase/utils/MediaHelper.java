package com.rora.phase.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.rora.phase.R;
import com.squareup.picasso.Picasso;

public class MediaHelper {

    public static void loadYoutubeVideo(Lifecycle lifeCycle, YouTubePlayerView v, String videoId) {
        lifeCycle.addObserver(v);
        v.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
    }

    public static void loadVideo(VideoView v, String url, boolean enableRepeatWhenDone) {
        v.setVideoPath(url);
        v.start();
        if (enableRepeatWhenDone)
            v.setOnCompletionListener(MediaPlayer::start);
    }

    public static boolean isYoutubeUrl(String youTubeURl)
    {
        boolean success;
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        if (!youTubeURl.isEmpty() && youTubeURl.matches(pattern))
        {
            success = true;
        }
        else
        {
            // Not Valid youtube URL
            success = false;
        }
        return success;
    }

    /**
     * Load image from url/uri
     * */

    public static void loadImage(ImageView intoView, String url) {
        Picasso.get().load(url).into(intoView);
    }

    public static void loadImage(ImageView intoView, String url, int placeHolder) {
        Picasso.get().load(url).placeholder(placeHolder).into(intoView);
    }

    /**
     * Load image from resource id
     * */

    public static void loadImage(ImageView intoView, int resourceId) {
        Picasso.get().load(resourceId).into(intoView);
    }

    public static void loadImage(ImageView intoView, int resourceId, int placeHolder) {
        Picasso.get().load(resourceId).placeholder(placeHolder).into(intoView);
    }

    public static void loadSvg(ImageView intoView, int url) {
        intoView.setImageResource(url);
    }

}
