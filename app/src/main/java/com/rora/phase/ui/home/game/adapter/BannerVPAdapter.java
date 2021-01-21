package com.rora.phase.ui.home.game.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.home.game.GameBannerViewHolder;

import java.util.ArrayList;
import java.util.List;

public class BannerVPAdapter  extends RecyclerView.Adapter<GameBannerViewHolder> {

    private List<Game> bannerList;

    public BannerVPAdapter() {
        this.bannerList = new ArrayList<>();
    }

    @NonNull
    @Override
    public GameBannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_banner, parent, false);

        return new GameBannerViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull GameBannerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public void bindData(List<Game> bannerList) {
        this.bannerList = bannerList;
        notifyDataSetChanged();
    }
}
