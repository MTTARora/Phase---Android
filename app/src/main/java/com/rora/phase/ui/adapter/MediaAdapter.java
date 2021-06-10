package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.model.MediaImage;
import com.rora.phase.model.ui.Media;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;
import com.rora.phase.utils.ui.MediaView;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import carbon.widget.ImageView;

public class MediaAdapter extends BaseRVAdapter {

    private List<Media> mediaList;
    private double widthPercent = 0;
    private boolean haveCorner;
    private boolean isPicture;

    public MediaAdapter(boolean isPicture, double customWidthPercent, boolean haveCorner) {
        this.mediaList = new ArrayList<>();
        this.widthPercent = customWidthPercent;
        this.haveCorner = haveCorner;
        this.isPicture = isPicture;
    }

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        super.onCreateViewHolder(parent, viewType);
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
        ((MediaVH)holder).bindData(mediaList.get(position), isPicture);

        if (haveCorner)
            ((MediaVH) holder).mediaView.setCornerRadius(context.getResources().getDimension(R.dimen.medium_radius));

        if (onItemSelectedListener != null)
            holder.itemView.setOnClickListener(v -> onItemSelectedListener.onSelected(position, mediaList.get(position)));
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    @Override
    public <T> void bindData(T mediaList) {
        this.mediaList = mediaList == null ? new ArrayList<>() : (List<Media>) mediaList;
        notifyDataSetChanged();
    }
}

class MediaVH extends BaseRVViewHolder {

    public MediaView mediaView;

    public MediaVH(@NonNull View itemView) {
        super(itemView);

        mediaView = itemView.findViewById(R.id.media_mv);
    }

    public void bindData(Media data, boolean isPicture) {
        if (data == null || data.getAvailableLink(Media.Quality.LOW) == null || data.getAvailableLink(Media.Quality.LOW).isEmpty())
            return;

        if (isPicture)
            mediaView.loadImage(data.getAvailableLink(Media.Quality.LOW));
        else
            mediaView.loadVideo(((MainActivity)itemView.getContext()).getLifecycle(), data.getAvailableLink(Media.Quality.LOW), MediaView.LIGHT_VIDEO_MODE);
    }

}
