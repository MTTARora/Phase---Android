package com.rora.phase.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.utils.callback.OnResultCallBack;
import com.rora.phase.utils.network.APIServicesHelper;
import com.rora.phase.utils.network.PhaseService;
import com.rora.phase.utils.network.PhaseServiceHelper;

import java.util.ArrayList;
import java.util.List;

public class GameRepository {

    private PhaseService gameServices;
    private PhaseService tagServices;

    private MutableLiveData<List<Game>> newGameList, recentPlayList, editorsChoiceList, hotGameList, trendingList, gameByCategoryList, gamesByPayTypeList, similarGameList;
    private MutableLiveData<List<Tag>> categoryList;

    private MutableLiveData<String> errMsg;

    public GameRepository() {
        newGameList = new MutableLiveData<>();
        recentPlayList = new MutableLiveData<>();
        editorsChoiceList = new MutableLiveData<>();
        hotGameList = new MutableLiveData<>();
        trendingList = new MutableLiveData<>();
        categoryList = new MutableLiveData<>();
        gameByCategoryList = new MutableLiveData<>();
        gamesByPayTypeList = new MutableLiveData<>();
        similarGameList = new MutableLiveData<>();

        errMsg = new MutableLiveData<>();

        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper();
        gameServices = phaseServiceHelper.getGamePhaseService();
        tagServices = phaseServiceHelper.getPhaseService();
    }


    //---------- GET SET ----------

    public LiveData<List<Game>> getNewGameList() {
        return newGameList;
    }

    public MutableLiveData<List<Game>> getRecentPlayList() {
        return recentPlayList;
    }

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

    public MutableLiveData<List<Game>> getSimilarGameList() {
        return similarGameList;
    }

    //----------------------------

    public void reset() {
        newGameList = new MutableLiveData<>();
        recentPlayList = new MutableLiveData<>();
        editorsChoiceList = new MutableLiveData<>();
        hotGameList = new MutableLiveData<>();
        trendingList = new MutableLiveData<>();
        categoryList = new MutableLiveData<>();
        gameByCategoryList = new MutableLiveData<>();
        gamesByPayTypeList = new MutableLiveData<>();
        similarGameList = new MutableLiveData<>();
    }

    public void getNewGameListData(int page, int pageSize) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getNewGames(page, pageSize), (err, data) -> {
            if (err != null) {
                newGameList.postValue(new ArrayList<>());
                errMsg.postValue(err);
            } else {
                List<Game> listGame = data;
                listGame = listGame == null ? new ArrayList<>() : listGame;
                newGameList.postValue(listGame);
            }
        });
    }

    public void getEditorsChoiceListData(int page, int pageSize) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getEditorsChoice(page, pageSize), (err, data) -> {
            if (err != null) {
                editorsChoiceList.postValue(new ArrayList<>());
                errMsg.postValue(err);
            } else {
                List<Game> listGame = data;
                listGame = listGame == null ? new ArrayList<>() : listGame;
                editorsChoiceList.postValue(listGame);
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
                List<Game> listGame = data;
                listGame = listGame == null ? new ArrayList<>() : listGame;
                hotGameList.postValue(listGame);
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
                List<Game> listGame = data;
                listGame = listGame == null ? new ArrayList<>() : listGame;
                trendingList.postValue(listGame);
            }
        });
    }

    public void getCategoryListData() {
        APIServicesHelper<List<Tag>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(tagServices.getCategoryList(), (err, data) -> {
            if (err != null) {
                categoryList.postValue(new ArrayList<>());
            } else {
                List<Tag> list = data;
                list = list == null ? new ArrayList<>() : list;
                categoryList.postValue(list);
                if(list.size() != 0)
                    getGamesByCategoryData(list.get(0).getTag() != null ? list.get(0).getTag() : "", 1, 20);
            }
        });
    }

    public void getGamesByCategoryData(String tagName, int page, int pageSize) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getGameByCategoryList(tagName, page, pageSize), (err, data) -> {
            if (err != null) {
                gameByCategoryList.postValue(new ArrayList<>());
                errMsg.postValue(err);
            } else {
                List<Game> listGame = data;
                listGame = listGame == null ? new ArrayList<>() : listGame;
                gameByCategoryList.postValue(listGame);
            }
        });
    }

    public void getSimilarGameListData(String gameId, int page, int pageSize) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getSimilarGameList(gameId, page, pageSize), (err, data) -> {
            if (err != null) {
                similarGameList.postValue(new ArrayList<>());
                errMsg.postValue(err);
            } else {
                List<Game> listGame = data;
                listGame = listGame == null ? new ArrayList<>() : listGame;
                similarGameList.postValue(listGame);
            }
        });
    }

    public void getGamesByPayTypeData(String payType, int page, int pageSize) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getGameByPayType(payType, page, pageSize), (err, data) -> {
            if (err != null) {
                gamesByPayTypeList.postValue(new ArrayList<>());
                errMsg.postValue(err);
            } else {
                List<Game> listGame = data;
                listGame = listGame == null ? new ArrayList<>() : listGame;
                gamesByPayTypeList.postValue(listGame);
            }
        });
    }

    public void getGameData(String gameId, OnResultCallBack onResultCallBack) {
        APIServicesHelper<Game> apiHelper = new APIServicesHelper<>();

        apiHelper.request(gameServices.getGame(gameId), (err, data) -> {
            if (err != null) {
                onResultCallBack.onResult(err, null);
            } else {
                Game game = data;
                game = game == null ? new Game() : game;
                onResultCallBack.onResult(null, game);
            }
        });
    }
}
