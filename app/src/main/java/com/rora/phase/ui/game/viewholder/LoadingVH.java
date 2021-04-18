package com.rora.phase.ui.game.viewholder;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.utils.ui.BaseRVViewHolder;

public class LoadingVH extends BaseRVViewHolder {

    public ProgressBar progressBar;

    public LoadingVH(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progressBar);
    }

}
