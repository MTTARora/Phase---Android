package com.rora.phase.ui.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.rora.phase.R;
import com.rora.phase.model.ui.FilterParams;

public class FilterLayout extends HorizontalScrollView {

    private TextView filterBtn, sortByBtn, tagFilterBtn, payTypeBtn, playTypeBtn, pegiBtn, nopBtn, byFeatureBtn, gameQltBtn;

    private FilterParams filterParams;

    private OnFilterClickListener onFilterClickListener;

    public interface OnFilterClickListener {
        void onClick(FilterParams filters);
    }

    public FilterLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.filters_layout, this);

        filterBtn = findViewById(R.id.filter_btn);
        sortByBtn = findViewById(R.id.sort_by_btn);
        tagFilterBtn = findViewById(R.id.tag_filter_btn);
        payTypeBtn = findViewById(R.id.pay_type_filter_btn);
        playTypeBtn = findViewById(R.id.play_types_filter_btn);
        pegiBtn = findViewById(R.id.pegi_filter_btn);
        nopBtn = findViewById(R.id.nop_filter_btn);
        byFeatureBtn = findViewById(R.id.feature_filter_btn);
        gameQltBtn = findViewById(R.id.game_qlt_filter_btn);

        filterBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.ALL));
        sortByBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.SORT_BY));
        tagFilterBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.TAG));
        payTypeBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.PAY_TYPES));
        playTypeBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.PLAY_TYPES));
        pegiBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.PEGI));
        nopBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.NUMBER_OF_PLAYER));
        byFeatureBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.BY_FEATURE));
        gameQltBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.GAME_QLT));

        filterParams = new FilterParams();

        findViewById(R.id.filter_layout_scrollview).setHorizontalScrollBarEnabled(false);
    }

    public void updateFilters(FilterParams filterParams) {
        if (filterParams == null) {
            return;
        }
        
        this.filterParams = filterParams;
        
        filterBtn.setVisibility(filterParams.getFilterType() == FilterParams.Filter.SORT_BY ? View.VISIBLE : View.GONE);
        sortByBtn.setVisibility(ContextCompat.getColor(getContext(), filterParams.getSortBy() != FilterParams.SortType.NONE ? R.color.colorPrimary : R.color.transparent));
        //tagFilterBtn.setVisibility(ContextCompat.getColor(getContext(), filterParams.getTa() != FilterParams.SortType.NONE ? R.color.colorPrimary : R.color.transparent));
        payTypeBtn.setVisibility(ContextCompat.getColor(getContext(), filterParams.getPayTypes().size() > 0 ? R.color.colorPrimary : R.color.transparent));
        playTypeBtn.setVisibility(ContextCompat.getColor(getContext(), filterParams.getPlayTypes().size() > 0 ? R.color.colorPrimary : R.color.transparent));
        pegiBtn.setVisibility(ContextCompat.getColor(getContext(), filterParams.getAgeRatings().size() > 0 ? R.color.colorPrimary : R.color.transparent));
        nopBtn.setVisibility(ContextCompat.getColor(getContext(), filterParams.getNumberOfPlayers().size() > 0 ? R.color.colorPrimary : R.color.transparent));
        byFeatureBtn.setVisibility(ContextCompat.getColor(getContext(), filterParams.getFeatureGamesType() != FilterParams.FeatureGamesType.ALL ? R.color.colorPrimary : R.color.transparent));
        //gameQltBtn.setVisibility(ContextCompat.getColor(getContext(), filterParams.getSortBy() != FilterParams.SortType.NONE ? R.color.colorPrimary : R.color.transparent));
    }

    private void notifyFilterSelected(FilterParams.Filter type) {
        filterParams.setFilterType(type);
        if (onFilterClickListener != null)
            onFilterClickListener.onClick(filterParams);
    }

    public void setOnFiltersClickListener(OnFilterClickListener onFiltersClickListener) {
        this.onFilterClickListener = onFiltersClickListener;
    }

}
