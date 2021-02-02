package com.rora.phase.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.MIN_SIZE;
import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.NORMAL_SIZE;

public class GameVerticalRecycerViewAdapter extends RecyclerView.Adapter<VerticalGameItemVH> {

    private List<Game> gameList;

    public GameVerticalRecycerViewAdapter() {
        this.gameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public VerticalGameItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertial_game, parent, false);

        return new VerticalGameItemVH(root);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalGameItemVH holder, int position) {
        holder.bindData(gameList.get(position));
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void bindData(List<Game> gameList) {
        this.gameList = gameList != null ? gameList : new ArrayList<>();
        notifyDataSetChanged();
    }

}

class VerticalGameItemVH extends RecyclerView.ViewHolder {

    private TextView tvGameName, tvPayType;
    private RecyclerView rclvTag;
    private RecyclerView rclvPlatform;

    private Context context;

    public VerticalGameItemVH(@NonNull View itemView) {
        super(itemView);
        tvGameName = itemView.findViewById(R.id.game_name_vertical_item_txv);
        tvPayType = itemView.findViewById(R.id.pay_type_tv);
        rclvTag = itemView.findViewById(R.id.category_item_game_rclv);
        rclvPlatform = itemView.findViewById(R.id.platform_rclv);

        rclvTag.setAdapter(new CategoryRecyclerViewAdapter(null, 0.11, MIN_SIZE, false));
        rclvTag.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL , false));
        rclvTag.setHasFixedSize(true);

        rclvPlatform.setAdapter(new PlatformRecyclerViewAdapter());
        rclvPlatform.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL , false));
        rclvPlatform.setHasFixedSize(true);

        this.context = itemView.getContext();
    }

    public void bindData(Game game) {
        tvGameName.setText(game.getName());
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

        ((CategoryRecyclerViewAdapter) Objects.requireNonNull(rclvTag.getAdapter())).bindData(game.getTags());
        ((PlatformRecyclerViewAdapter) Objects.requireNonNull(rclvPlatform.getAdapter())).bindData(game.getPlatforms());
    }

}
