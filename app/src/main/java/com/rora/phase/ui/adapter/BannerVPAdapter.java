package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Banner;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class BannerVPAdapter extends BaseRVAdapter {

    private List<Banner> bannerList;
    private double widthPercent = 0;

    public BannerVPAdapter(double customWidthPercent) {
        this.bannerList = new ArrayList<>();
        this.widthPercent = customWidthPercent;
    }

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull BaseRVViewHolder holder, int position) {
        holder.bindData(bannerList.get(position));
        if (onItemSelectedListener != null)
            holder.itemView.setOnClickListener(v -> onItemSelectedListener.onSelected(position, bannerList.get(position)));
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public void bindData(List<Banner> bannerList) {
        this.bannerList = bannerList == null ? new ArrayList<>() : bannerList;
        notifyDataSetChanged();
    }

    @Override
    public <T> void bindData(T bannerList) {
        this.bannerList = bannerList == null ? new ArrayList<>() : (List<Banner>) bannerList;
        notifyDataSetChanged();
    }
}

class BannerViewHolder extends BaseRVViewHolder {

    private ImageView bannerImv;

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);

        bannerImv = itemView.findViewById(R.id.banner_home);
    }

    @Override
    public <T> void bindData(T data) {

        MediaHelper.loadImage(bannerImv, ((Banner)data).getLink());
    }

}
