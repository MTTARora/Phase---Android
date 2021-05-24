package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.model.api.FilterQuery;
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
    private MutableLiveData<List<Tag>> tagList;

    public SearchViewModel(Application application) {
        super(application);
        gameRepository = new GameRepository();
        searchList = new MutableLiveData<>();
        suggestionList = new MutableLiveData<>();
        filters = new MutableLiveData<>();
        tagList = new MutableLiveData<>();

        filters.setValue(new FilterParams());
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

    public MutableLiveData<List<Tag>> getTagList() {
        return tagList;
    }

    public void setFilters(FilterParams filterParams) {
        this.filters.setValue(filterParams);
    }

    public void resetKeySearch() {
        filters.getValue().setName("");
    }


    // SERVICES

    public boolean isUserLogged() {
        return !SharedPreferencesHelper.newInstance(getApplication().getBaseContext()).getUserToken().isEmpty();
    }

    public void searchGame(String keySearch) {
        filters.getValue().setName(keySearch == null ? filters.getValue().getName() : keySearch);
        if (filters.getValue().getName() != null && !filters.getValue().getName().isEmpty() || !filters.getValue().isDefault())
            gameRepository.searchGame(new FilterQuery(filters.getValue()), (errMsg, data) -> {
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

    public void getTagListData() {
        gameRepository.getTagListData((errMsg, data) -> tagList.setValue(data));
    }

    public void reset() {
        searchList = new MutableLiveData<>();
        suggestionList = new MutableLiveData<>();
        filters = new MutableLiveData<>();
        tagList = new MutableLiveData<>();

        filters.setValue(new FilterParams());
    }
}
