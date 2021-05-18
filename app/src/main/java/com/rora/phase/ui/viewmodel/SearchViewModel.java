package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rora.phase.model.Game;
import com.rora.phase.model.api.SearchSuggestion;
import com.rora.phase.model.ui.FilterParams;
import com.rora.phase.repository.GameRepository;
import com.rora.phase.utils.SharedPreferencesHelper;
import com.rora.phase.utils.callback.OnResultCallBack;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private GameRepository gameRepository;
    private MutableLiveData<List<Game>> searchList;
    private MutableLiveData<List<SearchSuggestion>> suggestionList;
    private MutableLiveData<FilterParams> filters;

    public SearchViewModel(Application application) {
        super(application);
        gameRepository = new GameRepository();
        searchList = new MutableLiveData<>();
        suggestionList = new MutableLiveData<>();
        filters = new MutableLiveData<>();
    }

    public LiveData<List<Game>> getSearchResult() {
        return searchList;
    }

    public LiveData<List<SearchSuggestion>> getSuggestionList() {
        return suggestionList;
    }

    public LiveData<FilterParams> getFilters() {
        return filters;
    }

    public boolean isUserLogged() {
        return !SharedPreferencesHelper.newInstance(getApplication().getBaseContext()).getUserToken().isEmpty();
    }

    public void searchGame(String keySearch) {
        gameRepository.searchGame(keySearch, (errMsg, data) -> {
            if (errMsg != null) {
                searchList.setValue(null);
                return;
            }

            searchList.setValue(data);
        });
    }

    public void suggestSearch(String keySearch) {
        gameRepository.suggestSearch(keySearch, (err, data) -> {
            if (err != null) {
                suggestionList.setValue(null);
                return;
            }

            suggestionList.setValue(data);
        });
    }

    public void filter(FilterParams filters) {
        this.filters.setValue(filters);
    }

}
