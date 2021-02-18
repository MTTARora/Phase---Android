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

import java.util.Objects;

public class GameInfoViewHolder extends RecyclerView.ViewHolder {

    private ImageView imvBanner;
    private TextView tvName, tvPayType;
    private RecyclerView rclvPlatform;

    private Context context;
    private OnItemSelectedListener onItemSelectedListener;

    public GameInfoViewHolder(@NonNull View itemView) {
        super(itemView);
        imvBanner = itemView.findViewById(R.id.banner_game_info_imv);
        tvName = itemView.findViewById(R.id.game_name_info_tv);
        tvPayType = itemView.findViewById(R.id.pay_type_tv);
        rclvPlatform = itemView.findViewById(R.id.platform_rclv);

        rclvPlatform.setLayoutManager(new LinearLayoutManager(itemView.getContext(), RecyclerView.HORIZONTAL, false));
        rclvPlatform.setAdapter(new PlatformRecyclerViewAdapter());
        rclvPlatform.setHasFixedSize(true);

        this.context = itemView.getContext();
    }

    public void bindData(Game game) {
        MediaHelper.loadImage(game.getBackground(), imvBanner);
        tvName.setText(game.getName());
        tvPayType.setText(game.getPayTypeName());

        switch (game.getPayTypeId()) {
            case 1:
                tvPayType.setBackgroundColor(context.getColor(android.R.color.holo_blue_light));
                tvPayType.setTextColor(context.getColor(R.color.dim));
                break;
            case 2:
                tvPayType.setBackgroundColor(context.getColor(R.color.yellow));
                tvPayType.setTextColor(context.getColor(R.color.dim));
                break;
            case 3:
                tvPayType.setBackgroundColor(context.getColor(R.color.red));
                tvPayType.setTextColor(context.getColor(R.color.white));
                break;
            case 4:
                tvPayType.setBackgroundColor(context.getColor(R.color.green));
                tvPayType.setTextColor(context.getColor(R.color.green_dark));
                break;
            default:
                tvPayType.setBackgroundColor(context.getColor(R.color.gray));
                break;
        }
        ((PlatformRecyclerViewAdapter) Objects.requireNonNull(rclvPlatform.getAdapter())).bindData(game.getPlatforms());

        itemView.setOnClickListener(v -> {
            if (onItemSelectedListener != null)
                onItemSelectedListener.onSelected(game.getId().toString());
        });
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

}
