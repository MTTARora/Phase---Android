package com.rora.phase.utils;

import android.widget.ImageView;

import com.rora.phase.R;
import com.squareup.picasso.Picasso;

public class MediaHelper {


    /**
     * Load image from url/uri
     * */

    public static void loadImage(String url, ImageView intoView) {
        Picasso.get().load(url).into(intoView);
    }

    public static void loadImage(String url, ImageView intoView, int placeHolder) {
        Picasso.get().load(url).placeholder(placeHolder).into(intoView);
    }

    /**
     * Load image from resource id
     * */

    public static void loadImage(int resourceId, ImageView intoView) {
        Picasso.get().load(resourceId).into(intoView);
    }

    public static void loadImage(int resourceId, ImageView intoView, int placeHolder) {
        Picasso.get().load(resourceId).placeholder(placeHolder).into(intoView);
    }

    public static void loadSvg(int url, ImageView intoView) {
        intoView.setImageResource(url);
    }


}
