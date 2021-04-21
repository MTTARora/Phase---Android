package com.rora.phase.utils;

import android.widget.ImageView;

import com.rora.phase.R;
import com.squareup.picasso.Picasso;

public class MediaHelper {


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
