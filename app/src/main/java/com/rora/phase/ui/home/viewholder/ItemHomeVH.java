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
import com.rora.phase.ui.adapter.GameRVAdapter;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;

public class ItemHomeVH extends BaseRVViewHolder {

    private TextView tvSession;
    private RecyclerView rclvList;
    private ImageView errImv;
    private ImageButton btnViewAll;

    public ItemHomeVH(@NonNull View itemView) {
        super(itemView);

        tvSession = itemView.findViewById(R.id.session_home_item_tv);
        rclvList = itemView.findViewById(R.id.rclv_data_home_item);
        errImv = itemView.findViewById(R.id.error_data_home_item_imv);

        btnViewAll = itemView.findViewById(R.id.btn_view_all_home_item);
    }

    public void bindData(Context context, HomeUIData data) {
        tvSession.setText(data.getSessionName(context));
        errImv.setVisibility(data.gameList.size() == 0 ? View.VISIBLE : View.GONE);

        int viewType;
        switch (data.type) {
            case EDITOR:
                viewType = GameRVAdapter.VIEW_TYPE_LANDSCAPE;
                break;
            case HOT:
            case TRENDING:
                viewType = GameRVAdapter.VIEW_TYPE_SMALL_PORTRAIT;
                break;
            case NEW:
            default:
                viewType = GameRVAdapter.VIEW_TYPE_PORTRAIT;
                break;
        }

        if (rclvList.getAdapter() == null) {
        }

        setupGameRecyclerView(rclvList,
                new GameRVAdapter(viewType),
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ((GameRVAdapter)rclvList.getAdapter()).bindData(data.gameList);

        if (onItemSelectedListener != null && data.gameList != null && data.gameList.size() != 0)
            btnViewAll.setOnClickListener(v -> onItemSelectedListener.onSelected(getLayoutPosition(), data));
    }

    private void setupGameRecyclerView(RecyclerView view, BaseRVAdapter adapter, RecyclerView.LayoutManager layoutManager) {
        view.setLayoutManager(layoutManager);
        view.setAdapter(adapter);
        //view.setHasFixedSize(true);

        if (onChildItemClickListener != null)
            adapter.setOnItemSelectedListener((position, selectedItem) -> onChildItemClickListener.onSelected(getLayoutPosition(), selectedItem));
    }

}
