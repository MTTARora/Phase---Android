package com.rora.phase.utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.rora.phase.R;

public class CustomToolbar extends LinearLayout {

    public CustomToolbar(Context context) {
        this(context, null);
    }

    public CustomToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.custom_toolbar, this);

        findViewById(R.id.back_btn).setOnClickListener(v -> ((AppCompatActivity)context).onBackPressed());
    }

    public void showActionbar(String title, boolean enableBackBtn) {
        ViewCompat.setOnApplyWindowInsetsListener(this, (v, insets) -> {
            this.setPadding(0, insets.getSystemWindowInsetTop() + (int) getResources().getDimension(R.dimen.minnn_space), 0, 0);
            return insets;
        });
        setVisibility(VISIBLE);
        enableBackButton(enableBackBtn);
        setScreenTitle(title);
    }

    public void hideActionbar() {
        setVisibility(GONE);
        enableBackButton(false);
    }

    public void setScreenTitle(String title) {
        ((TextView)findViewById(R.id.title_custom_toolbar)).setText(title);
    }

    public void setActionbarStyle(float size, int color) {
        if (size != 0)
            ((TextView)findViewById(R.id.title_custom_toolbar)).setTextSize(size);
        if (color != 0)
            ((TextView)findViewById(R.id.title_custom_toolbar)).setTextColor(color);
    }

    private void enableBackButton(boolean enable) {
        ImageButton backBtn = findViewById(R.id.back_btn);
        if (enable) {
            backBtn.setVisibility(VISIBLE);
            backBtn.setOnClickListener(v -> ((AppCompatActivity)getContext()).onBackPressed());
        } else {
            backBtn.setVisibility(GONE);
            ViewHelper.setMargins(findViewById(R.id.custom_toolbar), (int) getResources().getDimension(R.dimen.normal_space), 0, (int) getResources().getDimension(R.dimen.normal_space), 0);
        }
    }

}
