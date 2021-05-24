package com.rora.phase.utils.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;

import com.rora.phase.R;

public class LoadingView extends FrameLayout {

    private ContentLoadingProgressBar progressBar;
    private ErrorView errorView;

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.loading_view, this);

        progressBar = findViewById(R.id.loading_pb);
        errorView = findViewById(R.id.error_loading_view);
    }

    public void startLoading() {
        setVisibility(VISIBLE);
        progressBar.setVisibility(VISIBLE);
        errorView.setVisibility(GONE);
    }

    /** @param err pass null to simplify stop loading */
    public void stopLoading(@Nullable String err, String actionTitle, OnClickListener actionCallback) {
        if (err == null) {
            setVisibility(GONE);
            return;
        }

        setVisibility(VISIBLE);
        errorView.setMsg(err);
        errorView.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);

        if (actionCallback != null)
            errorView.setAction(actionTitle, actionCallback);
        else
            errorView.setAction(null, null);
    }

}
