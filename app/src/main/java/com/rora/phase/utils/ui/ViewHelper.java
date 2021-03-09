package com.rora.phase.utils.ui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.annotation.Nullable;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ViewHelper {

    public static void setSize(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (width != 0)
            layoutParams.width = width;
        if (height != 0)
            layoutParams.height = height;

        view.setLayoutParams(layoutParams);
    }

    public static void setSizePercentageWithScreen(View view, @Nullable double widthPercentage, @Nullable double heightPercentage) {
        int width = ((AppCompatActivity)view.getContext()).getWindowManager().getDefaultDisplay().getWidth();
        int height = ((AppCompatActivity)view.getContext()).getWindowManager().getDefaultDisplay().getHeight();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (widthPercentage != 0)
            layoutParams.width = (int) (width * widthPercentage);
        if (heightPercentage != 0)
            layoutParams.height = (int) (height * heightPercentage);

        view.setLayoutParams(layoutParams);
    }

    public static void setSizePercentageWithScreenAndItSelf(View view, @Nullable double widthPercentage, @Nullable double heightPercentage, double scale) {
        int width = ((AppCompatActivity)view.getContext()).getWindowManager().getDefaultDisplay().getWidth();
        int height = ((AppCompatActivity)view.getContext()).getWindowManager().getDefaultDisplay().getHeight();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (widthPercentage != 0) {
            layoutParams.width = (int) (width * widthPercentage);
            layoutParams.height = (int) (layoutParams.width / scale);
        }
        if (heightPercentage != 0) {
            layoutParams.height = (int) (height * heightPercentage);
            layoutParams.width = (int) (layoutParams.height * scale);
        }

        view.setLayoutParams(layoutParams);
    }

    public static void setMargins(View v, @Nullable int left, @Nullable int top, @Nullable int right, @Nullable int bottom) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();

        left = left == 0 ? layoutParams.leftMargin : left;
        top = top == 0 ? layoutParams.topMargin : top;
        right = right == 0 ? layoutParams.rightMargin : right;
        bottom = bottom == 0 ? layoutParams.bottomMargin : bottom;

        layoutParams.setMargins(left, top, right, bottom); // left, top, right, bottom
        v.setLayoutParams(layoutParams);
    }

    public static LinearLayoutManager getLayoutManager(Activity activity, double widthPercentage, @Nullable double heightPercentage) {
        return new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                if (widthPercentage != 0)
                    lp.width = (int) (getWidth()/widthPercentage);
                if (heightPercentage != 0)
                    lp.width = (int) (getHeight()/heightPercentage);
                return super.checkLayoutParams(lp);
            }
        };
    }

}
