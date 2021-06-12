package com.rora.phase.utils.ui;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.utils.callback.OnItemSelectedListener;

public abstract class BaseRVViewHolder<T> extends RecyclerView.ViewHolder {

    protected OnItemSelectedListener onItemSelectedListener;
    protected OnItemSelectedListener onChildItemClickListener;

    public BaseRVViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void setOnChildItemClickListener(OnItemSelectedListener onChildItemClickListener) {
        this.onChildItemClickListener = onChildItemClickListener;
    }

    public void bindData(T data) {

    }

}
