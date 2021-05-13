package com.rora.phase.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.utils.MediaHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionListAdapter extends ArrayAdapter<Game> {

    private List<Game> gameList;

    public SearchSuggestionListAdapter(@NonNull Context context, List<Game> games) {
        super(context, 0, games);

        gameList = games;
    }

    @Override
    public int getCount() {
        return gameList.size();
    }

    @Override
    public Game getItem(int position) {
        return gameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_suggestion, parent, false);
        }

        MediaHelper.loadImage(convertView.findViewById(R.id.ic_suggestion), getItem(position).getBanner().getAvailableLink());
        ((TextView) convertView.findViewById(R.id.suggestion_game_name)).setText(getItem(position).getName());

        return convertView;
    }

    public void bindData(List<Game> games) {
        this.gameList = games == null ? new ArrayList<>() : games;
        notifyDataSetChanged();
    }

}
