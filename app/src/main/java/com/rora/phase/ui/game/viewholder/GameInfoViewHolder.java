package com.rora.phase.ui.game.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.Platform;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseRVViewHolder;

public class GameInfoViewHolder extends BaseRVViewHolder {

    private ImageView imvBanner;
    private TextView tvName, tvPayType;
    private RecyclerView rclvPlatform;

    public GameInfoViewHolder(@NonNull View itemView) {
        super(itemView);
        imvBanner = itemView.findViewById(R.id.banner_game_info_imv);
        tvName = itemView.findViewById(R.id.game_name_item_game_info_tv);
        tvPayType = itemView.findViewById(R.id.pay_type_item_game_info_tv);
        //rclvPlatform = itemView.findViewById(R.id.platform_item_game_info_rclv);
        //
        //rclvPlatform.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        //rclvPlatform.setAdapter(new PlatformRecyclerViewAdapter());
        //rclvPlatform.setHasFixedSize(true);
    }

    public void bindData(Game game) {
        MediaHelper.loadImage(imvBanner, game.getBackground());
        tvName.setText(game.getName());
        tvPayType.setText(game.getPayTypeName());
        //((PlatformRecyclerViewAdapter) Objects.requireNonNull(rclvPlatform.getAdapter())).bindData(game.getPlatforms());
        //rclvPlatform.invalidate();
        itemView.findViewById(R.id.ic_steam_imv).setVisibility(View.GONE);
        itemView.findViewById(R.id.ic_battle_net_imv).setVisibility(View.GONE);
        itemView.findViewById(R.id.ic_epic_imv).setVisibility(View.GONE);
        itemView.findViewById(R.id.ic_origin_imv).setVisibility(View.GONE);
        itemView.findViewById(R.id.ic_ubisoft_imv).setVisibility(View.GONE);
        itemView.findViewById(R.id.ic_garena_imv).setVisibility(View.GONE);
        itemView.findViewById(R.id.ic_riot_imv).setVisibility(View.GONE);
        for (Platform platform : game.getPlatforms()) {

            switch (platform.getPlatformName()) {
                case "Steam":
                    itemView.findViewById(R.id.ic_steam_imv).setVisibility(View.VISIBLE);
                    break;
                case "Battle.net":
                    itemView.findViewById(R.id.ic_battle_net_imv).setVisibility(View.VISIBLE);
                    break;
                case "Epic":
                    itemView.findViewById(R.id.ic_epic_imv).setVisibility(View.VISIBLE);
                    break;
                case "Origin":
                    itemView.findViewById(R.id.ic_origin_imv).setVisibility(View.VISIBLE);
                    break;
                case "Ubisoft":
                    itemView.findViewById(R.id.ic_ubisoft_imv).setVisibility(View.VISIBLE);
                    break;
                case "Garena":
                    itemView.findViewById(R.id.ic_garena_imv).setVisibility(View.VISIBLE);
                    break;
                case "Riot":
                    itemView.findViewById(R.id.ic_riot_imv).setVisibility(View.VISIBLE);
                    break;
                case "GOG":
                    break;
            }
        }

        itemView.setOnClickListener(v -> {
            if (onItemSelectedListener != null)
                onItemSelectedListener.onSelected(game);
        });
    }

}
