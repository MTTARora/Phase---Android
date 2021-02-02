package com.rora.phase.ui.game.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.utils.MediaHelper;

public class RecentPlayViewHolder extends RecyclerView.ViewHolder {

    private ImageView bannerImv;
    private TextView gameNameTv;

    public RecentPlayViewHolder(@NonNull View itemView) {
        super(itemView);
        bannerImv = itemView.findViewById(R.id.banner_recent_play);
        gameNameTv = itemView.findViewById(R.id.game_name_recent_play_tv);
    }

    public void bindData(Game game) {
        MediaHelper.loadImage(game.getBackground(), bannerImv);
        gameNameTv.setText(game.getName());
    }

}
