package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.model.Game;
import com.rora.phase.ui.home.game.GameMinInfoViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GameMinInfoRecyclerViewAdapter extends RecyclerView.Adapter<GameMinInfoViewHolder> {

    private List<Game> gameList;
    private int viewId;
    private double widthPercentage;

    public GameMinInfoRecyclerViewAdapter(int viewId, double widthPercentage) {
        this.viewId = viewId;
        this.widthPercentage = widthPercentage;
        this.gameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public GameMinInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(viewId, parent, false);
        if(widthPercentage != 0) {
            int width = ((AppCompatActivity)parent.getContext()).getWindowManager().getDefaultDisplay().getWidth();
            ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
            layoutParams.width = (int) (width * widthPercentage);
            root.setLayoutParams(layoutParams);
        }

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
