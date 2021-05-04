package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Banner;
import com.rora.phase.ui.home.viewholder.BannerViewHolder;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class BannerVPAdapter extends RecyclerView.Adapter<BannerViewHolder> {

    private List<Banner> bannerList;
    private double widthPercent = 0;

    public BannerVPAdapter(double customWidthPercent) {
        this.bannerList = new ArrayList<>();
        this.widthPercent = customWidthPercent;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        if (widthPercent != 0) {
            //ViewHelper.setSizePercentageWithScreenAndItSelf(root, widthPercent, 0, 1.5);
            ViewHelper.setSizePercentageWithScreen(root, widthPercent, 0);

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) root.getLayoutParams();

            layoutParams.setMargins(0, 0, (int) parent.getContext().getResources().getDimension(R.dimen.medium_space), 0); // left, top, right, bottom
            root.setLayoutParams(layoutParams);
        }
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
