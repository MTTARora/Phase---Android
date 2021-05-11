package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Banner;
import com.rora.phase.model.MediaImage;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends BaseRVAdapter {

    private List<MediaImage> mediaList;
    private double widthPercent = 0;

    public MediaAdapter(double customWidthPercent) {
        this.mediaList = new ArrayList<>();
        this.widthPercent = customWidthPercent;
    }

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        if (widthPercent != 0) {
            //ViewHelper.setSizePercentageWithScreenAndItSelf(root, widthPercent, 0, 1.5);
            ViewHelper.setSizePercentageWithScreen(root, widthPercent, 0);

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) root.getLayoutParams();

            layoutParams.setMargins(0, 0, (int) parent.getContext().getResources().getDimension(R.dimen.medium_space), 0); // left, top, right, bottom
            root.setLayoutParams(layoutParams);
        }
        return new MediaVH(root);
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

    public void bindData(List<MediaImage> mediaList) {
        this.mediaList = mediaList == null ? new ArrayList<>() : mediaList;
        notifyDataSetChanged();
    }

    @Override
    public <T> void bindData(T mediaList) {
        this.mediaList = mediaList == null ? new ArrayList<>() : (List<MediaImage>) mediaList;
        notifyDataSetChanged();
    }
}

class MediaVH extends BaseRVViewHolder {

    private ImageView imageImv;

    public MediaVH(@NonNull View itemView) {
        super(itemView);

        imageImv = itemView.findViewById(R.id.image_media_imv);
    }

    @Override
    public <T> void bindData(T data) {
        if (data == null || ((MediaImage)data).getAvailableLink() == null || ((MediaImage)data).getAvailableLink().isEmpty())
            return;

        itemView.findViewById(R.id.media_error).setVisibility(View.GONE);
        MediaHelper.loadImage(imageImv, ((MediaImage)data).getAvailableLink());
    }

}
