package com.rora.phase.ui.game.viewholder;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;

public class LoadingVH extends RecyclerView.ViewHolder {

    public ProgressBar progressBar;

    public LoadingVH(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progressBar);
    }

}
