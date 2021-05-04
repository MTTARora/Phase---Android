package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class RecentPlayVPAdapter extends BaseRVAdapter {

    private List<Game> gameList;

    public RecentPlayVPAdapter() {
        this.gameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_play, parent, false);
        ViewHelper.setSizePercentageWithScreenAndItSelf(root, 0, 0, 0.5);
        return new RecentPlayVH(root);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRVViewHolder holder, int position) {
        ((RecentPlayVH)holder).bindData(gameList.get(position));
        holder.setOnItemSelectedListener(selectedItem -> {
            if( onItemSelectedListener != null)
                onItemSelectedListener.onSelected(selectedItem);
        });

        holder.setOnChildItemClickListener(selectedItem -> {
            if (onChildItemClickListener != null)
                onChildItemClickListener.onSelected(selectedItem);
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void bindData(List<Game> gameList) {
        this.gameList = gameList == null ? new ArrayList<>() : gameList;
        notifyDataSetChanged();
    }

}

class RecentPlayVH extends BaseRVViewHolder {

    private ImageView imvBanner;
    private RecyclerView rclvPlatform;

    public RecentPlayVH(@NonNull View itemView) {
        super(itemView);

        imvBanner = itemView.findViewById(R.id.banner_item_recent_play);
        rclvPlatform = itemView.findViewById(R.id.platform_item_recent_play_rclv);

        rclvPlatform.setLayoutManager(new LinearLayoutManager(itemView.getContext(), RecyclerView.HORIZONTAL, false));
        rclvPlatform.setAdapter(new PlatformRVAdapter());
        rclvPlatform.setHasFixedSize(true);
    }

    public void bindData(Game game) {
        if (game == null)
            return;

        MediaHelper.loadImage(imvBanner, game.getBackground());
        ((PlatformRVAdapter)rclvPlatform.getAdapter()).bindData(game.getPlatforms());

        itemView.findViewById(R.id.play_item_recent_play_btn).setOnClickListener(v -> {
            if (onChildItemClickListener != null)
                onChildItemClickListener.onSelected(game);
        });

        itemView.setOnClickListener(v -> {
            if (onItemSelectedListener != null)
                onItemSelectedListener.onSelected(game);
        });
    }

}
