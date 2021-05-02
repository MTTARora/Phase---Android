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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaHelper {

    public static void loadYoutubeVideo(Lifecycle lifeCycle, YouTubePlayerView v, String url) {
        lifeCycle.addObserver(v);
        Pattern pattern = Pattern.compile("(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);

        v.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                if (matcher.find()) {
                    youTubePlayer.loadVideo(matcher.group(), 0);
                }
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
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        return !youTubeURl.isEmpty() && youTubeURl.matches(pattern);
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
        //Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE).into(intoView);
    }

}
