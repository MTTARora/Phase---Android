package com.rora.phase.utils.ui;

import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.utils.callback.OnItemSelectedListener;

public abstract class BaseRVAdapter extends RecyclerView.Adapter {

    protected OnItemSelectedListener onItemSelectedListener;

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

}