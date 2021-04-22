package com.rora.phase.ui.home;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.ui.HomeUIData;
import com.rora.phase.ui.home.viewholder.ItemHomeVH;
import com.rora.phase.ui.home.viewholder.ItemHomeWithCategoryVH;
import com.rora.phase.ui.viewmodel.HomeViewModel;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeRVAdapter extends BaseRVAdapter {

    private HomeViewModel homeViewModel;
    private Activity activity;
    private List<HomeUIData> dataList;
    private OnItemSelectedListener<HomeUIData> onViewAllClickListener;
    private OnItemSelectedListener<String> onCategoryClickListener;
    private OnItemSelectedListener<Game> onChildItemClickListener;

    public HomeRVAdapter(Activity activity) {
        this.activity = activity;
        this.dataList = new ArrayList<>();
        dataList.add(new HomeUIData(HomeUIData.Type.NEW, new ArrayList<>()));
        dataList.add(new HomeUIData(HomeUIData.Type.TRENDING, new ArrayList<>()));
        dataList.add(new HomeUIData(HomeUIData.Type.HOT, new ArrayList<>()));
        dataList.add(new HomeUIData(HomeUIData.Type.EDITOR, new ArrayList<>()));
        dataList.add(new HomeUIData(HomeUIData.Type.DISCOVER_BY_CATEGORY, new ArrayList<>(), new ArrayList<>()));
    }

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (HomeUIData.Type.valueOf(viewType)) {
            case DISCOVER_BY_CATEGORY:
                ItemHomeWithCategoryVH rootHomeCategory = new ItemHomeWithCategoryVH(inflater.inflate(R.layout.item_home_with_category, parent, false));

                homeViewModel = new ViewModelProvider((MainActivity)activity).get(HomeViewModel.class);
                homeViewModel.getGameByCategoryList().observe((LifecycleOwner)activity, ((ItemHomeWithCategoryVH) rootHomeCategory)::updateGameList);
                return rootHomeCategory;
            case NEW:
            case TRENDING:
            case HOT:
            case EDITOR:
            default:
                return new ItemHomeVH(inflater.inflate(R.layout.item_home, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRVViewHolder holder, int position) {
        if (onViewAllClickListener != null)
            holder.setOnItemSelectedListener(selectedItem -> onViewAllClickListener.onSelected((HomeUIData)selectedItem));
        if(onChildItemClickListener != null)
            holder.setOnChildItemClickListener(selectedItem -> onChildItemClickListener.onSelected((Game)selectedItem));

        if (holder instanceof ItemHomeVH) {
            ((ItemHomeVH)holder).bindData(activity, dataList.get(position));
        } else {
            ((ItemHomeWithCategoryVH)holder).bindData(activity, dataList.get(position));

            if (onCategoryClickListener != null)
                ((ItemHomeWithCategoryVH)holder).setOnCategoryClickListener(selectedItem -> onCategoryClickListener.onSelected((String)selectedItem));
        }

        //holder.bindData(context, dataList.get(position));
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
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void bindDataWithCategoryData(HomeUIData data) {
        if (dataList.get(4).type != HomeUIData.Type.DISCOVER_BY_CATEGORY)
            return;

        data.tagList = data.tagList == null ? new ArrayList<>() : data.tagList;
        dataList.set(4, data);

        notifyItemChanged(4);
    }

    public void setOnViewAllClickListener(OnItemSelectedListener<HomeUIData> onClickListener) {
        this.onViewAllClickListener = onClickListener;
    }

    public void setOnCategoryClickListener(OnItemSelectedListener<String> onCategoryClickListener) {
        this.onCategoryClickListener = onCategoryClickListener;
    }

    public void setOnChildItemClickListener(OnItemSelectedListener<Game> onChildItemClickListener) {
        this.onChildItemClickListener = onChildItemClickListener;
    }
}
