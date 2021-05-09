package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.MediaImage;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MediaViewerAdapter extends BaseRVAdapter {

    private List<MediaImage> mediaList;

    public MediaViewerAdapter(List<MediaImage> mediaList) {
        this.mediaList = mediaList == null ? new ArrayList<>() : mediaList;
    }

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_layout, parent, false);

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

class MediaViewerVH extends BaseRVViewHolder {

    private ImageView imageView;

    public MediaViewerVH(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.image_media_viewer_imv);
    }

    @Override
    public <T> void bindData(T data) {
        MediaHelper.loadImage(imageView, ((MediaImage)data).getAvailableLink());
    }

}
