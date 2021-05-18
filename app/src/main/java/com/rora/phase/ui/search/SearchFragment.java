package com.rora.phase.ui.search;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.api.SearchSuggestion;
import com.rora.phase.model.enums.GameListType;
import com.rora.phase.model.ui.FilterParams;
import com.rora.phase.ui.adapter.GameRVAdapter;
import com.rora.phase.ui.adapter.GameVerticalRVAdapter;
import com.rora.phase.ui.adapter.SearchSuggestionListAdapter;
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.ui.game.GameListFragment;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.ui.viewmodel.HomeViewModel;
import com.rora.phase.ui.viewmodel.SearchViewModel;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import carbon.widget.FrameLayout;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SearchFragment extends BaseFragment {

    private SearchView searchView;
    private ListView suggestionList;
    private FilterLayout filterLayout;
    private RecyclerView hotGamesRclv;
    private FrameLayout frameSuggestion;
    private ConstraintLayout frameHotGames;
    private RecyclerView searchResultRclv;
    private ContentLoadingProgressBar suggestionPb;

    private boolean enableSuggestion = true;

    private SearchViewModel searchViewModel;
    private GameViewModel gameViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        View root = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = root.findViewById(R.id.search);
        suggestionList = root.findViewById(R.id.search_suggestion_lv);
        frameSuggestion = root.findViewById(R.id.frame_suggestion_search);
        filterLayout = root.findViewById(R.id.filters_search);
        frameHotGames = root.findViewById(R.id.frame_hot_games_search);
        hotGamesRclv = root.findViewById(R.id.rclv_data_home_item);
        suggestionPb = root.findViewById(R.id.suggestion_pb);
        searchResultRclv = root.findViewById(R.id.search_result_rclv);

        ((TextView)root.findViewById(R.id.session_home_item_tv)).setText("Hot");
        setupViews(root);
        initData();

        root.findViewById(R.id.btn_view_all_home_item).setOnClickListener(v -> moveTo(GameListFragment.newInstance("Hot", GameListType.HOT, null), GameListFragment.class.getSimpleName(), true));
        filterLayout.setOnFiltersClickListener(type -> popupScreen(FilterFragment.newInstance(), FilterFragment.class.getSimpleName(), false));
        root.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return true;
        });

        return root;
    }

    private void setupViews(View root) {
        showLoadingScreen();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            root.setPadding(0, insets.getSystemWindowInsetTop() + (int) getResources().getDimension(R.dimen.minnn_space), 0, 0);
            return insets;
        });
        searchResultRclv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        searchResultRclv.setAdapter(new GameVerticalRVAdapter(searchResultRclv));
        searchResultRclv.hasFixedSize();
        ((GameVerticalRVAdapter) searchResultRclv.getAdapter()).setOnItemSelectedListener((OnItemSelectedListener<Game>) (position, selectedItem) -> moveTo(GameDetailFragment.newInstance(selectedItem), GameDetailFragment.class.getSimpleName(), true));

        hotGamesRclv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        hotGamesRclv.setAdapter(new GameRVAdapter(GameRVAdapter.VIEW_TYPE_LANDSCAPE));
        hotGamesRclv.hasFixedSize();
        ((GameRVAdapter)hotGamesRclv.getAdapter()).setOnItemSelectedListener((OnItemSelectedListener<Game>) (position, selectedItem) -> moveTo(GameDetailFragment.newInstance(selectedItem), GameDetailFragment.class.getSimpleName(), true));

        suggestionPb.hide();
        suggestionList.setAdapter(new SearchSuggestionListAdapter(getContext(), new ArrayList<>()));
        ((SearchSuggestionListAdapter) suggestionList.getAdapter()).setOnItemSelectedListener((position, selectedItem) -> {
            hideSoftKeyboard();
            enableSuggestion = false;
            searchView.setQuery(selectedItem.getName(), false);
            moveTo(GameDetailFragment.newInstance(Game.init(selectedItem)), GameDetailFragment.class.getSimpleName(), true);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewModel.searchGame(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!enableSuggestion) {
                    enableSuggestion = true;
                    ((SearchSuggestionListAdapter) suggestionList.getAdapter()).bindData(new ArrayList<>());
                    return false;
                }

                if (newText.isEmpty()) {
                    ((SearchSuggestionListAdapter) suggestionList.getAdapter()).bindData(new ArrayList<>());
                    if (searchResultRclv.getVisibility() == View.VISIBLE) {
                        frameHotGames.setVisibility(View.VISIBLE);
                        searchResultRclv.setVisibility(View.GONE);
                    }
                }
                else {
                    suggestionPb.show();
                    searchViewModel.suggestSearch(newText);
                }
                return false;
            }
        });
    }

    private void initData() {
        searchViewModel.getFilters().observe(getViewLifecycleOwner(), new Observer<FilterParams>() {
            @Override
            public void onChanged(FilterParams filterParams) {
                filterLayout.updateFilters(filterParams);
            }
        });

        searchViewModel.getSuggestionList().observe(getViewLifecycleOwner(), games -> {
            if (frameSuggestion.getVisibility() == View.GONE)
                frameSuggestion.setVisibility(View.VISIBLE);
            ((SearchSuggestionListAdapter) suggestionList.getAdapter()).bindData(searchView.getQuery().toString().isEmpty() ? new ArrayList<>() : games);
            suggestionPb.hide();
        });

        searchViewModel.getSearchResult().observe(getViewLifecycleOwner(), games -> {
            frameSuggestion.setVisibility(View.GONE);
            frameHotGames.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
            ((GameVerticalRVAdapter) searchResultRclv.getAdapter()).bindData(games);
        });

        gameViewModel.getHotGameList().observe(getViewLifecycleOwner(), games -> {
            hideLoadingScreen();
            ((GameRVAdapter) hotGamesRclv.getAdapter()).bindData(games);
        });

        gameViewModel.getHotGameListData(1, 20);
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        searchView.clearFocus();
    }

}