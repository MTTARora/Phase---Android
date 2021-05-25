package com.rora.phase.ui.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.model.ui.Media;
import com.rora.phase.ui.game.viewholder.LoadingVH;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.callback.ILoadMore;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameVerticalRVAdapter extends BaseRVAdapter {

    private List<Game> gameList;
    private ILoadMore loadMore;
    private boolean isLoading;
    //private int visibleThreshold = 5;
    //private int lastVisibleItem,totalItemCount;
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;

    public GameVerticalRVAdapter(RecyclerView recyclerView) {
        this.gameList = new ArrayList<>();

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager != null && gameList.size() != 0) {
                    //totalItemCount = linearLayoutManager.getItemCount();
                    //lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    //if(!isLoading && totalItemCount <= (lastVisibleItem+visibleThreshold))
                    Game lastVisibleGame = gameList.get(linearLayoutManager.findLastVisibleItemPosition());
                    if(!isLoading && gameList.get(gameList.size()-1).getId().equals(lastVisibleGame.getId()))
                    {
                        if(loadMore != null) {
                            gameList.add(null);
                            isLoading = true;
                            notifyDataSetChanged();
                            loadMore.onLoadMore();
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM)
        {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_vertical, parent, false);
            return new VerticalGameItemVH(root);
        }
        else if(viewType == VIEW_TYPE_LOADING)
        {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingVH(root);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRVViewHolder holder, int position) {
        if(holder instanceof VerticalGameItemVH)
        {
            ((VerticalGameItemVH) holder).bindData(gameList.get(position));
            ((VerticalGameItemVH) holder).setOnItemSelectedListener((pos, selectedItemId) -> {
                if(onItemSelectedListener != null)
                    onItemSelectedListener.onSelected(position, selectedItemId);
            });
        }
        else if(holder instanceof LoadingVH)
        {
            LoadingVH loadingViewHolder = (LoadingVH)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return gameList.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void bindData(List<Game> gameList) {
        if (isLoading && this.gameList.size() != 0 && gameList != null && gameList.size() != 0 && this.gameList.get(gameList.size()-1) == null) {
            this.gameList.remove(gameList.size()-1);
            setLoaded();
        }
        this.gameList = gameList != null ? gameList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setLoadMore(ILoadMore loadMore) {
        //this.loadMore = loadMore;
    }

    public void setLoaded() {
        isLoading = false;
    }

}

class VerticalGameItemVH extends BaseRVViewHolder {

    private TextView tvGameName, tvPayType, tvTag, tvDesc;
    private ImageView imvBanner;
    private RecyclerView rclvPlatform;

    public VerticalGameItemVH(@NonNull View itemView) {
        super(itemView);

        imvBanner = itemView.findViewById(R.id.banner_game_info_imv);
        tvGameName = itemView.findViewById(R.id.game_name_vertical_item_tv);
        tvPayType = itemView.findViewById(R.id.pay_type_tv);
        tvTag = itemView.findViewById(R.id.category_item_game);
        tvDesc = itemView.findViewById(R.id.game_desc_vertical_item_tv);
        rclvPlatform = itemView.findViewById(R.id.platform_rclv);

        rclvPlatform.setAdapter(new PlatformRVAdapter());
        rclvPlatform.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL , false));
        rclvPlatform.setHasFixedSize(true);
    }

    public void bindData(Game game) {
        MediaHelper.loadImage(imvBanner, game.getBanner().getAvailableLink(Media.Quality.LOW));
        tvGameName.setText(game.getName());
        tvPayType.setText(game.getPayTypeName());
        tvDesc.setText(Html.fromHtml(game.getDesc(), Html.FROM_HTML_MODE_COMPACT));
        tvTag.setText("");
        if (game.getTags() != null)
            for (Tag tag : game.getTags()) {
                tvTag.setText(tvTag.getText() + "#" + tag.getTag() + " ");
            }

        ((PlatformRVAdapter) Objects.requireNonNull(rclvPlatform.getAdapter())).bindData(game.getPlatforms());

        itemView.setOnClickListener(v -> {
            if (onItemSelectedListener != null)
                onItemSelectedListener.onSelected(getLayoutPosition(), game);
        });
    }

}
