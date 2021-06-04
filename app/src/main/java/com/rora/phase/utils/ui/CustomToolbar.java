package com.rora.phase.utils.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    }

    public void showActionbar(String title, boolean enableBackBtn, OnClickListener onBackPressed) {
        ViewCompat.setOnApplyWindowInsetsListener(this, (v, insets) -> {
            this.setPadding(0, insets.getSystemWindowInsetTop() + (int) getResources().getDimension(R.dimen.minnn_space), 0, 0);
            return insets;
        });
        setVisibility(VISIBLE);
        setBackButton(enableBackBtn, onBackPressed);
        setScreenTitle(title);
    }

    public void hideActionbar() {
        setVisibility(GONE);
        setBackButton(false, null);
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

    private void setBackButton(boolean enable, OnClickListener onBackPressed) {
        ImageButton backBtn = findViewById(R.id.back_btn_custom_toolbar);
        if (enable) {
            backBtn.setVisibility(VISIBLE);
            backBtn.setOnClickListener(v -> {
                if (onBackPressed != null)
                    onBackPressed.onClick(v);
                else
                    ((AppCompatActivity) getContext()).onBackPressed();
            });
        } else {
            backBtn.setVisibility(GONE);
            ViewHelper.setMargins(findViewById(R.id.custom_toolbar), (int) getResources().getDimension(R.dimen.normal_space), 0, (int) getResources().getDimension(R.dimen.normal_space), 0);
        }
    }

}
