package com.rora.phase.utils.ui;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.utils.callback.OnItemSelectedListener;

public abstract class BaseRVAdapter extends RecyclerView.Adapter<BaseRVViewHolder> {

    protected Context context;
    protected OnItemSelectedListener onItemSelectedListener;
    protected OnItemSelectedListener onChildItemClickListener;

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return null;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void setOnChildItemClickListener(OnItemSelectedListener onChildItemClickListener) {
        this.onChildItemClickListener = onChildItemClickListener;
    }

    public <T> void bindData(T data) {

    }

}
