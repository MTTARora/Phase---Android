package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.MediaImage;
import com.rora.phase.model.ui.Media;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class MediaViewerAdapter extends BaseRVAdapter {

    private List<MediaImage> mediaList;
    private double widthPercent;

    public MediaViewerAdapter(List<MediaImage> mediaList, double customWidth) {
        this.mediaList = mediaList == null ? new ArrayList<>() : mediaList;
        widthPercent = customWidth;
    }

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_viewer_fullscreen, parent, false);

        if (widthPercent != 0) {
            //ViewHelper.setSizePercentageWithScreenAndItSelf(root, widthPercent, 0, 1.5);
            ViewHelper.setSizePercentageWithScreen(root, widthPercent, 0);

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) root.getLayoutParams();

            layoutParams.setMargins(0, 0, (int) parent.getContext().getResources().getDimension(R.dimen.medium_space), 0); // left, top, right, bottom
            root.setLayoutParams(layoutParams);
        }

        return new MediaViewerVH(root);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRVViewHolder holder, int position) {
        holder.bindData(mediaList.get(position));
        if (onItemSelectedListener != null)
            holder.itemView.setOnClickListener(v -> onItemSelectedListener.onSelected(position, mediaList.get(position)));
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    @Override
    public <T> void bindData(T data) {
        mediaList = data == null ? new ArrayList<>() : (List<MediaImage>) data;
        notifyDataSetChanged();
    }

}

class MediaViewerVH extends BaseRVViewHolder<MediaImage> {

    private ImageView imageView;

    public MediaViewerVH(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.image_media_viewer_imv);
    }

    @Override
    public void bindData(MediaImage data) {
        MediaHelper.loadImage(imageView, data.getAvailableLink(Media.Quality.MEDIUM));
    }

}
