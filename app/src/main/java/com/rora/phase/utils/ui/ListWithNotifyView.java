package com.rora.phase.utils.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.utils.callback.OnItemSelectedListener;

import java.util.List;

import javax.annotation.Nullable;

import carbon.widget.ConstraintLayout;

public class ListWithNotifyView extends ConstraintLayout {

    private RecyclerView listRclv;
    private LoadingView loadingView;

    public ListWithNotifyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.custom_list_with_notify, this);

        listRclv = findViewById(R.id.list_rclv);
        loadingView = findViewById(R.id.loading_view_list_with_notify);
    }

    public void setupList(RecyclerView.LayoutManager layoutManager, BaseRVAdapter adapter) {
        listRclv.setLayoutManager(layoutManager);
        listRclv.setAdapter(adapter);
        listRclv.setHasFixedSize(true);
    }

    public void startLoading() {
        setVisibility(VISIBLE);
        loadingView.startLoading();
        listRclv.setVisibility(GONE);
    }

    public void stopLoading(String err) {
        listRclv.setVisibility(err != null && !err.isEmpty() ? GONE : VISIBLE);
        loadingView.stopLoading(err, null, null);
    }

    /**
     * @param checkData false if only need to get the adapter
     * */
    public BaseRVAdapter getAdapter(boolean checkData, List data) {
        if (checkData) {
            if (getVisibility() == GONE)
                setVisibility(VISIBLE);

            stopLoading(data != null && data.size() != 0 ? null : getResources().getString(R.string.nothing_here_txt));
        }

        return (BaseRVAdapter) listRclv.getAdapter();
    }

    public RecyclerView getListView() {
        return listRclv;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<Game> onItemSelectedListener) {
        ((BaseRVAdapter) listRclv.getAdapter()).setOnItemSelectedListener(onItemSelectedListener);
    }

}
