package com.rora.phase.ui.home.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.ui.HomeUIData;
import com.rora.phase.ui.adapter.GameInfoRecyclerViewAdapter;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.BaseRVAdapter;

public class ItemHomeViewHolder extends RecyclerView.ViewHolder {

    private TextView tvSession;
    private RecyclerView rclvList;
    private ImageView errImv;
    private ImageButton btnViewAll;

    private OnItemSelectedListener<HomeUIData> onViewAllClickListener;
    private OnItemSelectedListener<Game> onChildItemClickListener;

    public ItemHomeViewHolder(@NonNull View itemView) {
        super(itemView);

        tvSession = itemView.findViewById(R.id.session_home_item_tv);
        rclvList = itemView.findViewById(R.id.rclv_data_home_item);
        errImv = itemView.findViewById(R.id.error_data_home_item_imv);

        btnViewAll = itemView.findViewById(R.id.btn_view_all_home_item);
    }

    public void bindData(Context context, HomeUIData data) {
        tvSession.setText(data.getSessionName(context));
        errImv.setVisibility(data.gameList.size() == 0 ? View.VISIBLE : View.GONE);
        setupGameRecyclerView(rclvList,
                new GameInfoRecyclerViewAdapter(GameInfoRecyclerViewAdapter.VIEW_TYPE_NORMAL, 0),
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ((GameInfoRecyclerViewAdapter)rclvList.getAdapter()).bindData(data.gameList);

        if (onViewAllClickListener != null)
            btnViewAll.setOnClickListener(v -> onViewAllClickListener.onSelected(data));
    }

    public void setOnViewAllClickListener(OnItemSelectedListener<HomeUIData> onClickListener) {
        this.onViewAllClickListener = onClickListener;
    }

    public void setOnChildItemClickListener(OnItemSelectedListener<Game> onChildItemClickListener) {
        this.onChildItemClickListener = onChildItemClickListener;
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

}
