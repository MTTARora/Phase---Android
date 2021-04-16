package com.rora.phase.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.ui.HomeUIData;
import com.rora.phase.ui.game.viewholder.GameInfoViewHolder;
import com.rora.phase.ui.home.viewholder.ItemHomeViewHolder;
import com.rora.phase.utils.callback.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class HomeRVAdapter extends RecyclerView.Adapter<ItemHomeViewHolder> {

    private Context context;
    private List<HomeUIData> dataList;
    private OnItemSelectedListener<HomeUIData> onViewAllClickListener;
    private OnItemSelectedListener<Game> onChildItemClickListener;

    public HomeRVAdapter(Context context) {
        this.context = context;
        this.dataList = new ArrayList<>();
        dataList.add(new HomeUIData(HomeUIData.Type.NEW, new ArrayList<>()));
        dataList.add(new HomeUIData(HomeUIData.Type.TRENDING, new ArrayList<>()));
        dataList.add(new HomeUIData(HomeUIData.Type.HOT, new ArrayList<>()));
        dataList.add(new HomeUIData(HomeUIData.Type.EDITOR, new ArrayList<>()));
    }

    @NonNull
    @Override
    public ItemHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.item_home;
        switch (HomeUIData.Type.valueOf(viewType)) {
            case NEW:
                layoutId = R.layout.item_home;
                break;
            case TRENDING:
                layoutId = R.layout.item_home;
                break;
            case HOT:
                layoutId = R.layout.item_home;
                break;
            case EDITOR:
                layoutId = R.layout.item_home;
                break;
            default:
                layoutId = R.layout.item_home;
                return null;
        }

        return new ItemHomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHomeViewHolder holder, int position) {
        holder.bindData(context, dataList.get(position));
        holder.setOnViewAllClickListener(selectedItem -> {
            if (onViewAllClickListener != null)
                onViewAllClickListener.onSelected(selectedItem);
        });
        holder.setOnChildItemClickListener(selectedItem -> {
            if(onChildItemClickListener != null)
                onChildItemClickListener.onSelected(selectedItem);
        });
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).type.getValue();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void bindData(HomeUIData data) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).type == data.type) {
                dataList.set(i, data);
                break;
            }
        }

        notifyDataSetChanged();
    }

    public void setOnViewAllClickListener(OnItemSelectedListener<HomeUIData> onClickListener) {
        this.onViewAllClickListener = onClickListener;
    }

    public void setOnChildItemClickListener(OnItemSelectedListener<Game> onChildItemClickListener) {
        this.onChildItemClickListener = onChildItemClickListener;
    }
}
