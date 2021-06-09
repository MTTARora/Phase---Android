package com.rora.phase.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.picasso.transformations.BlurTransformation;

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

    public static void loadVideo(VideoView v, String url, boolean enableRepeatWhenDone, boolean autoPlay) {
        if (url == null || url.isEmpty())
            return;

        v.setVideoPath(url);

        if (autoPlay)
            v.start();

        if (enableRepeatWhenDone)
            v.setOnCompletionListener(MediaPlayer::start);
    }

    public static boolean isYoutubeUrl(String url)
    {
        if (url == null || url.isEmpty())
            return false;

        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        return url.matches(pattern);
    }

    /**
     * Load image from url/uri
     * */

    public static void loadImage(ImageView intoView, String url) {
        if (url == null || url.isEmpty()) {
            intoView.setImageResource(0);
            return;
        }

        Picasso.get().load(url).into(intoView);
    }

    public static void loadImage(Context context, ImageView intoView, String url, int placeHolder) {
        if (url == null || url.isEmpty()) {
            intoView.setImageResource(0);
            return;
        }

        Picasso.get().load(url).placeholder(placeHolder).into(intoView);
    }

    public static void loadImageAsBackground(Context context, View view, String url) {
        if (url == null || url.isEmpty()) {
            view.setBackground(null);
            return;
        }

        Picasso.get().load(url).transform(new BlurTransformation(context, 15, 1)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                view.setBackground(new BitmapDrawable(context.getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Log.d("TAG", "Prepare Load");
            }
        });
    }

    public static void loadImageWithBlur() {
        //Blurry.with(context).radius(25).sampling(2).onto(rootView)
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

    public static void loadImageWithBlurEffect(Context context, ImageView intoView, String url) {
        if (url == null || url.isEmpty()) {
            intoView.setImageResource(0);
            return;
        }

        Picasso.get().load(url).transform(new BlurTransformation(context, 50, 1)).into(intoView);
    }

}
