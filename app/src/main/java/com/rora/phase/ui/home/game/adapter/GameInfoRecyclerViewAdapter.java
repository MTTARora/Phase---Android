package com.rora.phase.ui.home.game.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.home.game.GameInfoViewHolder;
import com.rora.phase.ui.home.game.GameMinInfoViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GameInfoRecyclerViewAdapter extends RecyclerView.Adapter<GameInfoViewHolder> {

    private List<Game> gameList;

    public GameInfoRecyclerViewAdapter() {
        this.gameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public GameInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_info, parent, false);

        int width = ((AppCompatActivity)parent.getContext()).getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
        layoutParams.width = (int) (width * 0.75);
        root.setLayoutParams(layoutParams);

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
