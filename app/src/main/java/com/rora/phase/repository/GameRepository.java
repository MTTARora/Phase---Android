package com.rora.phase.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.utils.DataResultHelper;
import com.rora.phase.utils.network.BaseResponse;
import com.rora.phase.utils.network.PhaseService;
import com.rora.phase.utils.network.PhaseServiceHelper;
import com.rora.phase.utils.network.UserPhaseService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameRepository {

    private PhaseService gameServices;
    private PhaseService tagServices;
    private UserPhaseService userServices;
    private MutableLiveData<List<Game>> newGameList, recentPlayList, editorsChoiceList, hotGameList, trendingList, gameByCategoryList, gamesByPayTypeList;
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

        errMsg = new MutableLiveData<>();

        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper();
        gameServices = phaseServiceHelper.getGamePhaseService();
        tagServices = phaseServiceHelper.getPhaseService();
        userServices = phaseServiceHelper.getUserPhaseService();
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

    //----------------------------


    public void getNewGameListData() {
        //List<Game> dumGameList = new ArrayList<>();
        //dumGameList.add(new Game("1", "Game 1", "link"));
        //dumGameList.add(new Game("2", "Game 2", "link"));
        //dumGameList.add(new Game("3", "Game 3", "link"));
        //dumGameList.add(new Game("4", "Game 4", "link"));
        //newGameList.postValue(dumGameList);

        gameServices.getNewGames().enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                DataResultHelper<BaseResponse<List<Game>>> dataResponse = PhaseServiceHelper.handleResponse(response);
                if (dataResponse.getErrMsg() != null) {
                    newGameList.postValue(new ArrayList<>());
                    errMsg.postValue(dataResponse.getErrMsg());
                } else {
                    List<Game> listGame = BaseResponse.getResult(dataResponse.getData());
                    listGame = listGame == null ? new ArrayList<>() : listGame;
                    newGameList.postValue(listGame);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                newGameList.postValue(new ArrayList<>());
            }
        });
    }

    public void getRecentPlayListData() {
        userServices.getRecentPlay().enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                List<Game> listGame = BaseResponse.getResult(response.body());
                listGame = listGame == null ? new ArrayList<>() : listGame;
                recentPlayList.postValue(listGame);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                recentPlayList.postValue(new ArrayList<>());
            }
        });
    }

    public void getEditorsChoiceListData() {
        gameServices.getEditorsChoice().enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                List<Game> listGame = BaseResponse.getResult(response.body());
                listGame = listGame == null ? new ArrayList<>() : listGame;
                editorsChoiceList.postValue(listGame);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                editorsChoiceList.postValue(new ArrayList<>());
            }
        });
    }

    public void getHotGameListData() {
        gameServices.getHotGames().enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                List<Game> listGame = BaseResponse.getResult(response.body());
                listGame = listGame == null ? new ArrayList<>() : listGame;
                hotGameList.postValue(listGame);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                hotGameList.postValue(new ArrayList<>());
            }
        });
    }

    public void getTrendingData() {
        gameServices.getTrending().enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                List<Game> listGame = BaseResponse.getResult(response.body());
                listGame = listGame == null ? new ArrayList<>() : listGame;
                trendingList.postValue(listGame);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                trendingList.postValue(new ArrayList<>());
            }
        });
    }

    public void getCategoryListData() {
        tagServices.getCategoryList().enqueue(new Callback<BaseResponse<List<Tag>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Tag>>> call, Response<BaseResponse<List<Tag>>> response) {
                List<Tag> list = BaseResponse.getResult(response.body());
                list = list == null ? new ArrayList<>() : list;
                categoryList.postValue(list);
                getGamesByCategoryData(list.get(0).getTag() != null ? list.get(0).getTag() : "");
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Tag>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                categoryList.postValue(new ArrayList<>());
            }
        });
    }

    public void getGamesByCategoryData(String tagName) {
        gameServices.getGameByCategoryList(tagName).enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                List<Game> list = BaseResponse.getResult(response.body());
                list = list == null ? new ArrayList<>() : list;
                gameByCategoryList.postValue(list);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                gameByCategoryList.postValue(new ArrayList<>());
            }
        });
    }

    public void getGamesByPayTypeData(int payType) {
        gameServices.getGameByPayType().enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                List<Game> list = BaseResponse.getResult(response.body());
                list = list == null ? new ArrayList<>() : list;
                gamesByPayTypeList.postValue(list);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                gamesByPayTypeList.postValue(new ArrayList<>());
            }
        });
    }

}
