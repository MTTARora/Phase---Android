package com.rora.phase.ui.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.rora.phase.R;
import com.rora.phase.model.ui.FilterParams;

public class FilterLayout extends HorizontalScrollView {

    private TextView filterBtn, sortByBtn, tagFilterBtn, platformBtn, payTypeBtn, playTypeBtn, pegiBtn, nopBtn, byFeatureBtn, gameQltBtn;

    private OnFilterClickListener onFilterClickListener;

    public interface OnFilterClickListener {
        void onClick(FilterParams.Filter filters);
    }

    public FilterLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.filters_layout, this);

        filterBtn = findViewById(R.id.filter_btn);
        sortByBtn = findViewById(R.id.sort_by_btn);
        tagFilterBtn = findViewById(R.id.tag_filter_btn);
        platformBtn = findViewById(R.id.platforms_filter_btn);
        payTypeBtn = findViewById(R.id.pay_type_filter_btn);
        playTypeBtn = findViewById(R.id.play_types_filter_btn);
        pegiBtn = findViewById(R.id.pegi_filter_btn);
        nopBtn = findViewById(R.id.nop_filter_btn);
        byFeatureBtn = findViewById(R.id.feature_filter_btn);
        gameQltBtn = findViewById(R.id.game_qlt_filter_btn);

        filterBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.ALL));
        sortByBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.SORT_BY));
        tagFilterBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.TAG));
        platformBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.PLATFORMS));
        payTypeBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.PAY_TYPES));
        playTypeBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.PLAY_TYPES));
        pegiBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.PEGI));
        nopBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.NUMBER_OF_PLAYER));
        byFeatureBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.BY_FEATURE));
        gameQltBtn.setOnClickListener(v -> notifyFilterSelected(FilterParams.Filter.GAME_QLT));

        findViewById(R.id.filter_layout_scrollview).setHorizontalScrollBarEnabled(false);
    }

    public void updateFilters(FilterParams filterParams) {
        if (filterParams == null)
            return;

        if (filterParams.getSortBy() != FilterParams.SortType.NONE
                || !filterParams.getPayTypes().all
                || !filterParams.getPlayTypes().all
                || !filterParams.getAgeRatings().all
                || !filterParams.getNumberOfPlayers().all
                || filterParams.getFeatureGamesType() != FilterParams.FeatureGamesType.ALL
                || filterParams.isOnlyHdr() || filterParams.isOnlyRayTracing() || filterParams.isOnlyDlss())
            setBgColor(filterBtn, R.drawable.shape_corner_primary_color);
        else
            setBgColor(filterBtn, R.drawable.shape_corner_transparent);

        setBgColor(sortByBtn, filterParams.getSortBy() != FilterParams.SortType.NONE ? R.drawable.shape_corner_primary_color : R.drawable.shape_corner_transparent);
        setBgColor(platformBtn, !filterParams.getPlatforms().all ? R.drawable.shape_corner_primary_color : R.drawable.shape_corner_transparent);
        setBgColor(payTypeBtn, !filterParams.getPayTypes().all ? R.drawable.shape_corner_primary_color : R.drawable.shape_corner_transparent);
        setBgColor(playTypeBtn, !filterParams.getPlayTypes().all ? R.drawable.shape_corner_primary_color : R.drawable.shape_corner_transparent);
        setBgColor(pegiBtn, !filterParams.getAgeRatings().all ? R.drawable.shape_corner_primary_color : R.drawable.shape_corner_transparent);
        setBgColor(nopBtn, !filterParams.getNumberOfPlayers().all ? R.drawable.shape_corner_primary_color : R.drawable.shape_corner_transparent);
        setBgColor(byFeatureBtn, filterParams.getFeatureGamesType() != FilterParams.FeatureGamesType.ALL ? R.drawable.shape_corner_primary_color : R.drawable.shape_corner_transparent);
        setBgColor(gameQltBtn, filterParams.isOnlyHdr() || filterParams.isOnlyRayTracing() || filterParams.isOnlyDlss() ? R.drawable.shape_corner_primary_color : R.drawable.shape_corner_transparent);
    }

    private void notifyFilterSelected(FilterParams.Filter type) {
        if (onFilterClickListener != null)
            onFilterClickListener.onClick(type);
    }

    public void setOnFiltersClickListener(OnFilterClickListener onFiltersClickListener) {
        this.onFilterClickListener = onFiltersClickListener;
    }

    private void setBgColor(View view, int drawable) {
        view.setBackground(getContext().getDrawable(drawable));
    }

}
