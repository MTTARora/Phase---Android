package com.rora.phase.utils.ui;

import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.utils.callback.OnItemSelectedListener;

public abstract class BaseRVAdapter extends RecyclerView.Adapter<BaseRVViewHolder> {

    protected OnItemSelectedListener onItemSelectedListener;
    protected OnItemSelectedListener onChildItemClickListener;

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void setOnChildItemClickListener(OnItemSelectedListener onChildItemClickListener) {
        this.onChildItemClickListener = onChildItemClickListener;
    }

}
