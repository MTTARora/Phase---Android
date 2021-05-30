package com.rora.phase.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.model.api.FilterQuery;
import com.rora.phase.model.api.SearchSuggestion;
import com.rora.phase.model.ui.FilterParams;
import com.rora.phase.utils.callback.OnResultCallBack;
import com.rora.phase.utils.network.APIServicesHelper;
import com.rora.phase.utils.network.PhaseService;
import com.rora.phase.utils.network.PhaseServiceHelper;

import java.util.ArrayList;
import java.util.List;

public class GameRepository {

    private PhaseService gameServices;
    private PhaseService gameAuthServices;
    private PhaseService tagServices;

    private MutableLiveData<List<Game>> editorsChoiceList, hotGameList, trendingList, gameByCategoryList, gamesByPayTypeList;
    private MutableLiveData<List<Tag>> categoryList;

    private MutableLiveData<String> errMsg;

    public GameRepository() {
        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper();
        gameServices = phaseServiceHelper.getGamePhaseService(false);
        gameAuthServices = phaseServiceHelper.getGamePhaseService(true);
        tagServices = phaseServiceHelper.getPhaseService();

        editorsChoiceList = new MutableLiveData<>();
        hotGameList = new MutableLiveData<>();
        trendingList = new MutableLiveData<>();
        categoryList = new MutableLiveData<>();
        gameByCategoryList = new MutableLiveData<>();
        gamesByPayTypeList = new MutableLiveData<>();

        errMsg = new MutableLiveData<>();
    }


    //---------- GET SET ----------

    public MutableLiveData<List<Game>> getEditorsChoiceList() {
        return editorsChoiceList;
    }

    public MutableLiveData<List<Game>> getHotGameList() {
        return hotGameList;
    }

    public MutableLiveData<List<Game>> getTrendingList() {
        return trendingList;
    }

    public MutableLiveData<List<Tag>> getCategoryList() {
        return categoryList;
    }

    public MutableLiveData<List<Game>> getGameByCategoryList() {
        return gameByCategoryList;
    }

    public MutableLiveData<List<Game>> getGamesByPayTypeList() {
        return gamesByPayTypeList;
    }

    //----------------------------

    public void reset() {
        editorsChoiceList = new MutableLiveData<>();
        hotGameList = new MutableLiveData<>();
        trendingList = new MutableLiveData<>();
        categoryList = new MutableLiveData<>();
        gameByCategoryList = new MutableLiveData<>();
        gamesByPayTypeList = new MutableLiveData<>();
    }

    public void getNewGameListData(int page, int pageSize, OnResultCallBack<List<Game>> onResultCallBack) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getNewGames(page, pageSize), (err, data) -> {
            if (err != null)
                onResultCallBack.onResult(err, null);
            else
                onResultCallBack.onResult(null, data == null ? new ArrayList<>() : data);
        });
    }

    public void getEditorsChoiceListData(int page, int pageSize) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getEditorsChoice(page, pageSize), (err, data) -> {
            if (err != null) {
                editorsChoiceList.postValue(new ArrayList<>());
                errMsg.postValue(err);
            } else {
                editorsChoiceList.postValue(data == null ? new ArrayList<>() : data);
            }
        });
    }

    public void getHotGameListData(int page, int pageSize) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getHotGames(page, pageSize), (err, data) -> {
            if (err != null) {
                hotGameList.postValue(new ArrayList<>());
                errMsg.postValue(err);
            } else {
                hotGameList.postValue(data == null ? new ArrayList<>() : data);
            }
        });
    }

    public void getTrendingData(int page, int pageSize) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getTrending(page, pageSize), (err, data) -> {
            if (err != null) {
                trendingList.postValue(new ArrayList<>());
                errMsg.postValue(err);
            } else {
                trendingList.postValue(data == null ? new ArrayList<>() : data);
            }
        });
    }

    public void getTagListAndGamesByFirstCategoryData() {
        APIServicesHelper<List<Tag>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(tagServices.getCategoryList(), (err, data) -> {
            if (err != null) {
                categoryList.postValue(new ArrayList<>());
            } else {
                List<Tag> list = data == null ? new ArrayList<>() : data;
                categoryList.postValue(list);
                if (list.size() != 0)
                    getGamesByCategoryData(list.get(0).getTag() != null ? list.get(0).getTag() : "", 1, 20);
            }
        });
    }

    public void getTagListData(OnResultCallBack<List<Tag>> resultCallBack) {

        APIServicesHelper<List<Tag>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(tagServices.getCategoryList(), (err, data) -> {
            if (err != null)
                resultCallBack.onResult(err, null);
            else
                resultCallBack.onResult(null, data == null ? new ArrayList<>() : data);
        });
    }

    public void getGamesByCategoryData(String tagName, int page, int pageSize) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getGameByCategoryList(tagName, page, pageSize), (err, data) -> {
            if (err != null) {
                gameByCategoryList.postValue(new ArrayList<>());
                errMsg.postValue(err);
            } else {
                gameByCategoryList.postValue(data == null ? new ArrayList<>() : data);
            }
        });
    }

    public void getSimilarGameListData(String gameId, int page, int pageSize, OnResultCallBack<List<Game>> resultCallBack) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getSimilarGameList(gameId, page, pageSize), (err, data) -> {
            if (err != null)
                resultCallBack.onResult(err, null);
            else
                resultCallBack.onResult(null, data == null ? new ArrayList<>() : data);
        });
    }

    public void getGamesByPayTypeData(String payType, int page, int pageSize) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getGameByPayType(payType, page, pageSize), (err, data) -> {
            if (err != null) {
                gamesByPayTypeList.postValue(new ArrayList<>());
                errMsg.postValue(err);
            } else {
                gamesByPayTypeList.postValue(data == null ? new ArrayList<>() : data);
            }
        });
    }

    public void getGameData(String gameId, boolean withAuth, OnResultCallBack<Game> onResultCallBack) {
        APIServicesHelper<Game> apiHelper = new APIServicesHelper<>();

        apiHelper.request((withAuth ? gameAuthServices : gameServices).getGame(gameId), (err, data) -> {
            if (err != null)
                onResultCallBack.onResult(err, null);
            else
                onResultCallBack.onResult(null, data == null ? new Game() : data);
        });
    }

    public void searchGame(FilterQuery params, OnResultCallBack<List<Game>> onResultCallBack) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.search(params), (err, data) -> {
            if (err != null)
                onResultCallBack.onResult(err, null);
            else
                onResultCallBack.onResult(null, data);

        });
    }

    public void suggestSearch(String keySearch, OnResultCallBack<List<SearchSuggestion>> onResultCallBack) {
        APIServicesHelper<List<SearchSuggestion>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.suggestSearch(keySearch), (err, data) -> {
            if (err != null)
                onResultCallBack.onResult(err, null);
            else
                onResultCallBack.onResult(null, data);
        });
    }
}
