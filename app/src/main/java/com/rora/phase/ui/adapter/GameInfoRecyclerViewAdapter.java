package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.game.viewholder.GameInfoViewHolder;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class GameInfoRecyclerViewAdapter extends RecyclerView.Adapter<GameInfoViewHolder> {

    private int viewType;
    private double widthPercentage;
    private List<Game> gameList;

    public static final int VIEW_TYPE_NORMAL = 0, VIEW_TYPE_LAYER = 1;

    public GameInfoRecyclerViewAdapter(int viewType, double widthPercentage) {
        this.viewType = viewType;
        this.widthPercentage = widthPercentage;
        this.gameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public GameInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(this.viewType == VIEW_TYPE_NORMAL ? R.layout.item_game_info : R.layout.item_editor_choice, parent, false);

        ViewHelper.setSizePercentageWithScreen(root, widthPercentage, 0);

        return new GameInfoViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull GameInfoViewHolder holder, int position) {
        holder.bindData(gameList.get(position));
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void bindData(List<Game> gameList) {
        this.gameList = gameList;
        notifyDataSetChanged();
    }

}
