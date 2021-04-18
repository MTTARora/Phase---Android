package com.rora.phase.ui.game.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.adapter.PlatformRecyclerViewAdapter;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.BaseRVViewHolder;

import java.util.Objects;

public class GameInfoViewHolder extends BaseRVViewHolder {

    private ImageView imvBanner;
    private TextView tvName, tvPayType;
    private RecyclerView rclvPlatform;

    public GameInfoViewHolder(@NonNull View itemView) {
        super(itemView);
        imvBanner = itemView.findViewById(R.id.banner_game_info_imv);
        tvName = itemView.findViewById(R.id.game_name_item_game_info_tv);
        tvPayType = itemView.findViewById(R.id.pay_type_item_game_info_tv);
        rclvPlatform = itemView.findViewById(R.id.platform_item_game_info_rclv);

        rclvPlatform.setLayoutManager(new LinearLayoutManager(itemView.getContext(), RecyclerView.HORIZONTAL, false));
        rclvPlatform.setAdapter(new PlatformRecyclerViewAdapter());
        rclvPlatform.setHasFixedSize(true);
    }

    public void bindData(Game game) {
        MediaHelper.loadImage(imvBanner, game.getBackground());
        tvName.setText(game.getName());
        tvPayType.setText(game.getPayTypeName());
        ((PlatformRecyclerViewAdapter) Objects.requireNonNull(rclvPlatform.getAdapter())).bindData(game.getPlatforms());

        itemView.setOnClickListener(v -> {
            if (onItemSelectedListener != null)
                onItemSelectedListener.onSelected(game);
        });
    }

}
