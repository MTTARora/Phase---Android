package com.rora.phase.ui.home.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Banner;
import com.rora.phase.utils.MediaHelper;

public class BannerViewHolder extends RecyclerView.ViewHolder {

    private ImageView bannerImv;

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);

        bannerImv = itemView.findViewById(R.id.banner_home);
    }

    public void bindData(Banner banner) {
        MediaHelper.loadImage(banner.getLink(), bannerImv);
    }

}