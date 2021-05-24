package com.rora.phase.utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rora.phase.R;

public class ErrorView extends LinearLayout {

    private ImageView errImv;
    private TextView msgTv;
    private Button actionBtn;

    public ErrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.error_layout, this);
        errImv = findViewById(R.id.error_imv);
        msgTv = findViewById(R.id.error_tv);
        actionBtn = findViewById(R.id.error_action_btn);
    }

    public ErrorView(Context context) {
        super(context);
    }

    public void setMsg(String msg) {
        msgTv.setText(msg);
    }

    public void setAction(String title, OnClickListener onClickListener) {
        if (title != null && !title.isEmpty()) {
            actionBtn.setVisibility(VISIBLE);
            actionBtn.setText(title);
            actionBtn.setOnClickListener(onClickListener);
        } else
            actionBtn.setVisibility(GONE);
    }

}
