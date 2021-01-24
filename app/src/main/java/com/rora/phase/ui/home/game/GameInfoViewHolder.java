package com.rora.phase.ui.home.game;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.utils.MediaHelper;

public class GameInfoViewHolder extends RecyclerView.ViewHolder {

    private ImageView bannerImv;
    private TextView nameTv;

    public GameInfoViewHolder(@NonNull View itemView) {
        super(itemView);
        bannerImv = itemView.findViewById(R.id.banner_game_info_imv);
        nameTv = itemView.findViewById(R.id.game_name_info_tv);
    }

    public void bindData(Game game) {
        MediaHelper.loadImage(game.getBackground(), bannerImv);
        nameTv.setText(game.getName());
    }

}
