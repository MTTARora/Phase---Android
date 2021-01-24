package com.rora.phase.ui.home.game;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.model.Game;
import com.rora.phase.R;
import com.rora.phase.utils.MediaHelper;

public class GameMinInfoViewHolder extends RecyclerView.ViewHolder {

    private ImageView bannerImv;
    private TextView gameNameTv;

    public GameMinInfoViewHolder(@NonNull View itemView) {
        super(itemView);
        bannerImv = itemView.findViewById(R.id.banner_game_min_info_imv);
        gameNameTv = itemView.findViewById(R.id.game_name_min_info_tv);

    }

    public void bindData(Game game) {
        MediaHelper.loadImage(game.getBackground(), bannerImv);
        gameNameTv.setText(game.getName());
    }

}
