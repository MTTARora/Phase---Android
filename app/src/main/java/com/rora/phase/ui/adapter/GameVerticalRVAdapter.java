package com.rora.phase.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.game.viewholder.LoadingVH;
import com.rora.phase.utils.callback.ILoadMore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.MIN_SIZE;
import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.NORMAL_SIZE;

public class GameVerticalRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM)
        {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertial_game, parent, false);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  VerticalGameItemVH)
        {
            ((VerticalGameItemVH) holder).bindData(gameList.get(position));
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
        if (isLoading && this.gameList.size() != 0 && this.gameList.get(gameList.size()-1) == null) {
            this.gameList.remove(gameList.size()-1);
            setLoaded();
        }
        this.gameList = gameList != null ? gameList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    public void setLoaded() {
        isLoading = false;
    }

}

class VerticalGameItemVH extends RecyclerView.ViewHolder {

    private TextView tvGameName, tvPayType;
    private RecyclerView rclvTag;
    private RecyclerView rclvPlatform;

    private Context context;

    public VerticalGameItemVH(@NonNull View itemView) {
        super(itemView);
        tvGameName = itemView.findViewById(R.id.game_name_vertical_item_txv);
        tvPayType = itemView.findViewById(R.id.pay_type_tv);
        rclvTag = itemView.findViewById(R.id.category_item_game_rclv);
        rclvPlatform = itemView.findViewById(R.id.platform_rclv);

        rclvTag.setAdapter(new CategoryRecyclerViewAdapter(0.11, MIN_SIZE, false, null));
        rclvTag.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL , false));
        rclvTag.setHasFixedSize(true);

        rclvPlatform.setAdapter(new PlatformRecyclerViewAdapter());
        rclvPlatform.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL , false));
        rclvPlatform.setHasFixedSize(true);

        this.context = itemView.getContext();
    }

    public void bindData(Game game) {
        tvGameName.setText(game.getName());
        tvPayType.setText(game.getPayTypeName());
        switch (game.getPayTypeId()) {
            case 1:
                tvPayType.setBackgroundColor(context.getColor(android.R.color.holo_blue_light));
                tvPayType.setTextColor(context.getColor(R.color.dim));
                break;
            case 2:
                tvPayType.setBackgroundColor(context.getColor(R.color.yellow));
                tvPayType.setTextColor(context.getColor(R.color.dim));
                break;
            case 3:
                tvPayType.setBackgroundColor(context.getColor(R.color.red));
                tvPayType.setTextColor(context.getColor(R.color.white));
                break;
            case 4:
                tvPayType.setBackgroundColor(context.getColor(R.color.green));
                tvPayType.setTextColor(context.getColor(R.color.green_dark));
                break;
            default:
                tvPayType.setBackgroundColor(context.getColor(R.color.gray));
                break;
        }

        ((CategoryRecyclerViewAdapter) Objects.requireNonNull(rclvTag.getAdapter())).bindData(game.getTags());
        ((PlatformRecyclerViewAdapter) Objects.requireNonNull(rclvPlatform.getAdapter())).bindData(game.getPlatforms());
    }

}
