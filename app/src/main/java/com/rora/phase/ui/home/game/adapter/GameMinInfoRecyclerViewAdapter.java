package com.rora.phase.ui.home.game.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.model.Game;
import com.rora.phase.R;
import com.rora.phase.ui.home.game.BannerViewHolder;
import com.rora.phase.ui.home.game.GameMinInfoViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GameMinInfoRecyclerViewAdapter extends RecyclerView.Adapter<GameMinInfoViewHolder> {

    private List<Game> gameList;

    public GameMinInfoRecyclerViewAdapter() {
        this.gameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public GameMinInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_min_info, parent, false);

        return new GameMinInfoViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull GameMinInfoViewHolder holder, int position) {
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
