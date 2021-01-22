package com.rora.phase.utils;

import android.widget.ImageView;

import com.rora.phase.R;
import com.squareup.picasso.Picasso;

public class MediaHelper {

    public static void loadImage(String url, ImageView intoView) {
        Picasso.get().load(url).into(intoView);
    }

    public static void loadImage(String url, ImageView intoView, int placeHolder) {
        Picasso.get().load(url).placeholder(placeHolder).into(intoView);
    }

}
