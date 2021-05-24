package com.rora.phase.ui.search;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ContentLoadingProgressBar;
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
import com.rora.phase.model.enums.GameListType;
import com.rora.phase.ui.adapter.GameRVAdapter;
import com.rora.phase.ui.adapter.GameVerticalRVAdapter;
import com.rora.phase.ui.adapter.SearchSuggestionListAdapter;
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.ui.game.GameListFragment;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.ui.viewmodel.SearchViewModel;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.ListWithNotifyView;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SearchFragment extends BaseFragment {

    private SearchView searchView;
    private ListView suggestionLv;
    private TextView tvSuggestionMsg;
    private FilterLayout filterLayout;
    private RecyclerView hotGamesRclv;
    private carbon.widget.ConstraintLayout frameSuggestion;
    private ConstraintLayout frameHotGames;
    private ListWithNotifyView searchResultView;
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
        suggestionLv = root.findViewById(R.id.search_suggestion_lv);
        tvSuggestionMsg = root.findViewById(R.id.suggestion_msg_tv);
        frameSuggestion = root.findViewById(R.id.frame_suggestion_search);
        filterLayout = root.findViewById(R.id.filters_search);
        frameHotGames = root.findViewById(R.id.frame_hot_games_search);
        hotGamesRclv = root.findViewById(R.id.rclv_data_home_item);
        suggestionPb = root.findViewById(R.id.suggestion_pb);
        searchResultView = root.findViewById(R.id.search_result_view);

        ((TextView)root.findViewById(R.id.session_home_item_tv)).setText("Hot");
        setupViews(root);
        initData();

        root.findViewById(R.id.btn_view_all_home_item).setOnClickListener(v -> moveTo(GameListFragment.newInstance("Hot", GameListType.HOT, null), GameListFragment.class.getSimpleName(), true));
        filterLayout.setOnFiltersClickListener(type -> {
            hideSoftKeyboard();
            moveTo(FilterFragment.newInstance(type), FilterFragment.class.getSimpleName(), false);
        });
        root.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return true;
        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchViewModel.reset();
    }

    private void setupViews(View root) {
        showLoadingScreen();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            root.setPadding(0, insets.getSystemWindowInsetTop() + (int) getResources().getDimension(R.dimen.minnn_space), 0, 0);
            return insets;
        });

        searchResultView.setupList(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false), new GameVerticalRVAdapter(searchResultView.getListView()));
        searchResultView.setOnItemSelectedListener((position, selectedItem) -> moveTo(GameDetailFragment.newInstance(selectedItem), GameDetailFragment.class.getSimpleName(), true));

        suggestionLv.setAdapter(new SearchSuggestionListAdapter(getContext(), new ArrayList<>()));
        ((SearchSuggestionListAdapter) suggestionLv.getAdapter()).setOnItemSelectedListener((position, selectedItem) -> {
            hideSoftKeyboard();
            enableSuggestion = false;
            searchView.setQuery(selectedItem.getName(), false);
            updateSuggestionUI(false, false, false);
            moveTo(GameDetailFragment.newInstance(Game.init(selectedItem)), GameDetailFragment.class.getSimpleName(), true);
        });

        hotGamesRclv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        hotGamesRclv.setAdapter(new GameRVAdapter(GameRVAdapter.VIEW_TYPE_LANDSCAPE));
        hotGamesRclv.hasFixedSize();
        ((GameRVAdapter)hotGamesRclv.getAdapter()).setOnItemSelectedListener((OnItemSelectedListener<Game>) (position, selectedItem) -> {
            hideSoftKeyboard();
            moveTo(GameDetailFragment.newInstance(selectedItem), GameDetailFragment.class.getSimpleName(), true);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchResultView.startLoading();
                enableSuggestion = false;
                frameSuggestion.setVisibility(View.GONE);
                searchViewModel.searchGame(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!enableSuggestion && !newText.isEmpty()) {
                    enableSuggestion = true;
                    updateSuggestionUI(false, false, false);
                    return false;
                }

                if (newText.isEmpty() && searchViewModel.getFilters().getValue().isDefault()) {
                    updateSuggestionUI(false, false, false);
                    if (searchResultView.getVisibility() == View.VISIBLE) {
                        frameHotGames.setVisibility(View.VISIBLE);
                        searchResultView.setVisibility(View.GONE);
                    }
                    searchViewModel.resetKeySearch();
                }
                else {
                    updateSuggestionUI(true, false, false);
                    searchViewModel.suggestSearch(newText);
                }
                return false;
            }
        });
    }

    private void initData() {
        searchViewModel.getFilters().observe(getViewLifecycleOwner(), filterParams -> {
            filterLayout.updateFilters(filterParams);
            if (!searchView.getQuery().toString().isEmpty() || !filterParams.isDefault()) {
                searchResultView.startLoading();
                searchViewModel.searchGame(null);
            } else {
                frameSuggestion.setVisibility(View.GONE);
                frameHotGames.setVisibility(View.VISIBLE);
                searchResultView.setVisibility(View.GONE);
            }
        });

        searchViewModel.getSuggestionList().observe(getViewLifecycleOwner(), games -> {
            if (enableSuggestion) {
                ((SearchSuggestionListAdapter) suggestionLv.getAdapter()).bindData(searchView.getQuery().toString().isEmpty() ? new ArrayList<>() : games);
                updateSuggestionUI(!searchView.getQuery().toString().isEmpty(), !searchView.getQuery().toString().isEmpty(), !(games != null && games.size() != 0));
            }
        });

        searchViewModel.getSearchResult().observe(getViewLifecycleOwner(), games -> {
            if (!searchView.getQuery().toString().isEmpty() || !searchViewModel.getFilters().getValue().isDefault()) {
                frameHotGames.setVisibility(games == null || games.size() == 0 ? View.VISIBLE : View.GONE);
                searchResultView.setVisibility(View.VISIBLE);
                ((GameVerticalRVAdapter) searchResultView.getAdapter(true, games)).bindData(games);
            }
        });

        gameViewModel.getHotGameList().observe(getViewLifecycleOwner(), games -> {
            hideLoadingScreen();
            ((GameRVAdapter) hotGamesRclv.getAdapter()).bindData(games);
        });

        gameViewModel.getHotGameListData(1, 20);
        searchViewModel.getTagListData();
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        searchView.clearFocus();
    }

    private void updateSuggestionUI(boolean showSuggestions, boolean showItemList, boolean showMsg) {
        if (searchView.getQuery().toString().isEmpty() || !showSuggestions) {
            if (frameSuggestion.getVisibility() == View.VISIBLE)
                frameSuggestion.setVisibility(View.GONE);
            ((SearchSuggestionListAdapter) suggestionLv.getAdapter()).bindData(new ArrayList<>());
            suggestionPb.setVisibility(View.VISIBLE);
            return;
        }

        if (frameSuggestion.getVisibility() == View.GONE)
            frameSuggestion.setVisibility(View.VISIBLE);

        if (!showItemList && !showMsg) {
            ((SearchSuggestionListAdapter) suggestionLv.getAdapter()).bindData(new ArrayList<>());
            suggestionPb.setVisibility(View.VISIBLE);
        } else
            suggestionPb.setVisibility(View.INVISIBLE);

        if (showMsg) {
            tvSuggestionMsg.setVisibility(View.VISIBLE);
            ((SearchSuggestionListAdapter) suggestionLv.getAdapter()).bindData(new ArrayList<>());
        }
        else
            tvSuggestionMsg.setVisibility(View.GONE);
    }

}