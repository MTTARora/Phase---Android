package com.rora.phase.ui.home.viewholder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.model.ui.HomeUIData;
import com.rora.phase.ui.adapter.CategoryRVAdapter;
import com.rora.phase.ui.adapter.GameRVAdapter;
import com.rora.phase.ui.home.HomeRVAdapter;
import com.rora.phase.ui.viewmodel.HomeViewModel;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import static com.rora.phase.ui.adapter.CategoryRVAdapter.MEDIUM_SIZE;

public class ItemHomeWithCategoryVH extends BaseRVViewHolder {

    private TextView tvSession;
    private RecyclerView rclvCategoryList, rclvGameList;
    private ImageView errImv;
    private ImageButton btnViewAll;

    public ItemHomeWithCategoryVH(@NonNull View itemView) {
        super(itemView);

        tvSession = itemView.findViewById(R.id.session_home_item_tv);
        rclvCategoryList = itemView.findViewById(R.id.category_item_home_rclv);
        rclvGameList = itemView.findViewById(R.id.rclv_data_home_item);
        errImv = itemView.findViewById(R.id.error_data_home_item_imv);

        btnViewAll = itemView.findViewById(R.id.btn_view_all_home_item);

        setupGameRecyclerView(rclvCategoryList,
                new CategoryRVAdapter(MEDIUM_SIZE, false),
                new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));

        setupGameRecyclerView(rclvGameList,
                new GameRVAdapter(GameRVAdapter.VIEW_TYPE_LANDSCAPE),
                new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));

    }

    public void bindData(Activity activity, HomeUIData data) {
        tvSession.setText(data.getSessionName(activity));

        ((CategoryRVAdapter) rclvCategoryList.getAdapter()).bindData(data.tagList);

        if (onItemSelectedListener != null)
            btnViewAll.setOnClickListener(v -> onItemSelectedListener.onSelected(data));
    }

    public void setOnCategoryClickListener(OnItemSelectedListener<String> onCategoryClickListener) {
        if(onCategoryClickListener != null)
            ((CategoryRVAdapter) rclvCategoryList.getAdapter()).setOnItemSelectedListener(selectedItem -> onCategoryClickListener.onSelected((String)selectedItem));
    }

    private void setupGameRecyclerView(RecyclerView view, BaseRVAdapter adapter, RecyclerView.LayoutManager layoutManager) {
        view.setAdapter(adapter);
        view.setLayoutManager(layoutManager);
        view.setHasFixedSize(true);

        adapter.setOnItemSelectedListener(selectedItem -> {
            if (onChildItemClickListener != null)
                onChildItemClickListener.onSelected((Game)selectedItem);
        });
    }

    public void updateGameList(List<Game> games) {
        errImv.setVisibility(games.size() == 0 ? View.VISIBLE : View.GONE);
        ((GameRVAdapter) rclvGameList.getAdapter()).bindData(games);
    }
}
