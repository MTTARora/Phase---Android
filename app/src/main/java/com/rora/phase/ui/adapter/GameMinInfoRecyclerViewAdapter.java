package com.rora.phase.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.game.viewholder.GameMinInfoViewHolder;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.BaseRVAdapter;

import java.util.ArrayList;
import java.util.List;

public class GameMinInfoRecyclerViewAdapter extends BaseRVAdapter {

    private List<Game> gameList;
    private int viewType;
    private double widthPercentage;

    public static final int VIEW_TYPE_NORMAL = 0, VIEW_TYPE_EXPANDED = 1;

    public GameMinInfoRecyclerViewAdapter(int viewType, double widthPercentage) {
        this.viewType = viewType;
        this.widthPercentage = widthPercentage;
        this.gameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public GameMinInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(this.viewType == VIEW_TYPE_NORMAL ? R.layout.item_game_min_info : R.layout.item_game_min_info_expanded, parent, false);
        if(widthPercentage != 0) {
            int width = ((AppCompatActivity)parent.getContext()).getWindowManager().getDefaultDisplay().getWidth();
            ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
            layoutParams.width = (int) (width * widthPercentage);
            root.setLayoutParams(layoutParams);
        }

        return new GameMinInfoViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((GameMinInfoViewHolder) holder).bindData(gameList.get(position));

        ((GameMinInfoViewHolder) holder).setOnItemSelectedListener(selectedItemId -> {
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
