package com.rora.phase.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.RoraLog;
import com.rora.phase.model.Platform;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.UiHelper;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class PlatformRecyclerViewAdapter extends RecyclerView.Adapter<PlatformViewHolder> {

    private List<Platform> platformList;

    public PlatformRecyclerViewAdapter() {
        this.platformList = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlatformViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_platform, parent, false);
        ViewHelper.setSizePercentageWithScreen(root, 0.035, 0);
        return new PlatformViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatformViewHolder holder, int position) {
        holder.bindData(platformList.get(position));
    }

    @Override
    public int getItemCount() {
        return platformList.size();
    }

    public void bindData(List<Platform> platformList) {
        this.platformList = platformList != null ? platformList : new ArrayList<>();
        notifyDataSetChanged();
    }

}

class PlatformViewHolder extends RecyclerView.ViewHolder {
    private ImageView imvPlatform;

    public PlatformViewHolder(@NonNull View itemView) {
        super(itemView);

        imvPlatform = itemView.findViewById(R.id.ic_platform_imv);
    }

    public void bindData(Platform platform) {
        int srcId = 0;
        switch (platform.getPlatformName()) {
            case "Steam":
                srcId = R.drawable.ic_steam;
                break;
            case "Battle.net":
                srcId = R.drawable.ic_battle_net;
                break;
            case "Epic":
                srcId = R.drawable.ic_epic_games;
                break;
            case "Origin":
                srcId = R.drawable.ic_origin;
                break;
            case "Ubisoft":
                srcId = R.drawable.ic_ubisoft;
                break;
            case "Garena":
                srcId = R.drawable.ic_garena;
                break;
            case "Riot":
                srcId = R.drawable.ic_riot;
                break;
            case "GOG":
                srcId = R.drawable.ic_gog_com;
                break;
        }

        MediaHelper.loadSvg(imvPlatform, srcId);
    }
}
