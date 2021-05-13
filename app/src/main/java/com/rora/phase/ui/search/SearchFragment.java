package com.rora.phase.ui.search;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.adapter.GameRVAdapter;
import com.rora.phase.ui.adapter.SearchSuggestionListAdapter;
import com.rora.phase.ui.viewmodel.GameViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private ListView suggestionList;
    private ContentLoadingProgressBar suggestionPb;

    private GameViewModel gameViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        View root = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = root.findViewById(R.id.search);
        suggestionList = root.findViewById(R.id.search_suggestion_lv);
        suggestionPb = root.findViewById(R.id.suggestion_pb);

        setupViews(root);
        initData();

        root.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return true;
        });
        return root;
    }

    private void setupViews(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            root.setPadding(0, insets.getSystemWindowInsetTop() + (int) getResources().getDimension(R.dimen.minnn_space), 0, 0);
            return insets;
        });

        suggestionPb.hide();
        suggestionList.setAdapter(new SearchSuggestionListAdapter(getContext(), new ArrayList<>()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty())
                    ((SearchSuggestionListAdapter) suggestionList.getAdapter()).bindData(new ArrayList<>());
                else {
                    suggestionPb.show();
                    gameViewModel.searchGame(newText);
                }
                return false;
            }
        });
    }

    private void initData() {
        gameViewModel.getSearchResult().observe(getViewLifecycleOwner(), games -> {
            ((SearchSuggestionListAdapter) suggestionList.getAdapter()).bindData(games);
            suggestionPb.hide();
        });
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

}