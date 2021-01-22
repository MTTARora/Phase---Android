package com.rora.phase.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.utils.network.BaseResponse;
import com.rora.phase.utils.network.PhaseService;
import com.rora.phase.utils.network.PhaseServiceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameRepository {

    private PhaseService phaseService;
    private MutableLiveData<List<Game>> newGameList, recentPlayList, editorsChoiceList, hotGameList;

    public GameRepository() {
        newGameList = new MutableLiveData<>();
        recentPlayList = new MutableLiveData<>();
        editorsChoiceList = new MutableLiveData<>();
        hotGameList = new MutableLiveData<>();

        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper();
        phaseService = phaseServiceHelper.getGamePhaseService();
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

    //----------------------------


    public void getNewGameListData() {
        //List<Game> dumGameList = new ArrayList<>();
        //dumGameList.add(new Game("1", "Game 1", "link"));
        //dumGameList.add(new Game("2", "Game 2", "link"));
        //dumGameList.add(new Game("3", "Game 3", "link"));
        //dumGameList.add(new Game("4", "Game 4", "link"));
        //newGameList.postValue(dumGameList);

        phaseService.getNewGames().enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                List<Game> listGame = BaseResponse.getResult(response.body());
                listGame = listGame == null ? new ArrayList<>() : listGame;
                newGameList.postValue(listGame);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                newGameList.postValue(new ArrayList<>());
            }
        });
    }

    public void getRecentPlayListData() {
        phaseService.getRecentPlay().enqueue(new Callback<BaseResponse<List<Game>>>() {
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
        phaseService.getEditorsChoice().enqueue(new Callback<BaseResponse<List<Game>>>() {
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
        phaseService.getHotGames().enqueue(new Callback<BaseResponse<List<Game>>>() {
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

}
