package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.game.viewholder.GameInfoViewHolder;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class GameRVAdapter extends BaseRVAdapter {

    private int viewType;
    private List<Game> gameList;

    public static final int VIEW_TYPE_PORTRAIT = 0, VIEW_TYPE_LANDSCAPE = 1;

    public GameRVAdapter(int viewType) {
        this.viewType = viewType;
        this.gameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(this.viewType == VIEW_TYPE_PORTRAIT ? R.layout.item_game_portrait : R.layout.item_game_landscape, parent, false);

        return new GameInfoViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRVViewHolder holder, int position) {

        ((GameInfoViewHolder) holder).bindData(gameList.get(position));

        ((GameInfoViewHolder) holder).setOnItemSelectedListener(selectedItemId -> {
            if(onItemSelectedListener != null)
                onItemSelectedListener.onSelected(selectedItemId);
        });

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
