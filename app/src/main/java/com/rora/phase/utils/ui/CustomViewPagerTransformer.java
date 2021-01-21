package com.rora.phase.utils.ui;

import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.rora.phase.R;

import static java.lang.Math.abs;

public class CustomViewPagerTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {

        int nextItemVisiblePx  = 44;
        int currentItemHorizontalMarginPx = 62;
        int margin = nextItemVisiblePx + currentItemHorizontalMarginPx;

        page.setTranslationX(-margin * position);
        // Next line scales the item's height. You can remove it if you don't want this effect
        page.setScaleY(1 - (0.15f * abs(position)));
        // If you want a fading effect uncomment the next line:
        // page.alpha = 0.25f + (1 - abs(position))

    }

}
