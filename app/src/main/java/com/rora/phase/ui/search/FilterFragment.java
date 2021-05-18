package com.rora.phase.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.rora.phase.R;
import com.rora.phase.model.ui.FilterParams;
import com.rora.phase.ui.viewmodel.SearchViewModel;
import com.rora.phase.utils.ui.BaseFragment;

public class FilterFragment extends BaseFragment {

    private LinearLayout frameSortBy, framePayTypes, framePlayTypes, frameAgeRating, frameNOP, frameFeature, frameGameQlt, frameTag;

    private SearchViewModel searchViewModel;
    private FilterParams filters;

    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        View root = inflater.inflate(R.layout.fragment_filter, container, false);
        frameSortBy = root.findViewById(R.id.frame_sort_by_filter);
        framePayTypes = root.findViewById(R.id.frame_pay_types_filter);
        framePlayTypes = root.findViewById(R.id.frame_play_types_filter);
        frameAgeRating = root.findViewById(R.id.frame_age_filter);
        frameNOP = root.findViewById(R.id.frame_nop_filter);
        frameFeature = root.findViewById(R.id.frame_feature_filter);
        frameGameQlt = root.findViewById(R.id.frame_game_qlt_filter);
        frameTag = root.findViewById(R.id.frame_tag_filter);

        root.findViewById(R.id.apply_filter_btn).setOnClickListener(v -> applyFilters());

        showActionbar(root, "Filters", true);
        initData();
        return root;
    }

    private void updateViews() {
        if (filters == null) {
            frameSortBy.setVisibility(View.GONE);
            framePayTypes.setVisibility(View.GONE);
            framePlayTypes.setVisibility(View.GONE);
            frameAgeRating.setVisibility(View.GONE);
            frameNOP.setVisibility(View.GONE);
            frameFeature.setVisibility(View.GONE);
            frameGameQlt.setVisibility(View.GONE);
            frameTag.setVisibility(View.GONE);
            return;
        }

        frameSortBy.setVisibility(filters.getFilterType() == FilterParams.Filter.ALL || filters.getFilterType() == FilterParams.Filter.SORT_BY ? View.VISIBLE : View.GONE);
        framePayTypes.setVisibility(filters.getFilterType() == FilterParams.Filter.ALL || filters.getFilterType() == FilterParams.Filter.PAY_TYPES ? View.VISIBLE : View.GONE);
        framePlayTypes.setVisibility(filters.getFilterType() == FilterParams.Filter.ALL || filters.getFilterType() == FilterParams.Filter.PLAY_TYPES ? View.VISIBLE : View.GONE);
        frameAgeRating.setVisibility(filters.getFilterType() == FilterParams.Filter.ALL || filters.getFilterType() == FilterParams.Filter.PEGI ? View.VISIBLE : View.GONE);
        frameNOP.setVisibility(filters.getFilterType() == FilterParams.Filter.ALL || filters.getFilterType() == FilterParams.Filter.NUMBER_OF_PLAYER ? View.VISIBLE : View.GONE);
        frameFeature.setVisibility(filters.getFilterType() == FilterParams.Filter.ALL || filters.getFilterType() == FilterParams.Filter.BY_FEATURE ? View.VISIBLE : View.GONE);
        frameGameQlt.setVisibility(filters.getFilterType() == FilterParams.Filter.ALL || filters.getFilterType() == FilterParams.Filter.GAME_QLT ? View.VISIBLE : View.GONE);
        frameTag.setVisibility(filters.getFilterType() == FilterParams.Filter.ALL || filters.getFilterType() == FilterParams.Filter.TAG ? View.VISIBLE : View.GONE);
    }

    private void initData() {
        searchViewModel.getFilters().observe(getViewLifecycleOwner(), filterParams -> {
            filters = filterParams;
            updateViews();
        });
    }

    private void applyFilters() {
        //onFilterChangedListener.onFilterChanged(filters);
        searchViewModel.filter(filters);
        getActivity().onBackPressed();
    }

}
