package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Banner;
import com.rora.phase.ui.home.game.BannerViewHolder;

import java.util.ArrayList;
import java.util.List;

public class BannerVPAdapter  extends RecyclerView.Adapter<BannerViewHolder> {

    private List<Banner> bannerList;

    public BannerVPAdapter() {
        this.bannerList = new ArrayList<>();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);

        return new BannerViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bindData(bannerList.get(position));
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public void bindData(List<Banner> bannerList) {
        this.bannerList = bannerList == null ? new ArrayList<>() : bannerList;
        notifyDataSetChanged();
    }
}
