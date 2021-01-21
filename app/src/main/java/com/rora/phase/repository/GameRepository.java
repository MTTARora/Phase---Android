package com.rora.phase.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.utils.ServerHelper;
import com.rora.phase.utils.network.PhaseService;
import com.rora.phase.utils.network.PhaseServiceHelper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameRepository {

    private PhaseService phaseService;
    private MutableLiveData<List<Game>> newGameList, recentPlay, editorsChoiceList, hotGameList;

    public GameRepository() {
        newGameList = new MutableLiveData<>();
        recentPlay = new MutableLiveData<>();
        editorsChoiceList = new MutableLiveData<>();
        hotGameList = new MutableLiveData<>();

        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper();
        phaseService = phaseServiceHelper.getPhaseService();
    }


    //---------- GET SET ----------

    public LiveData<List<Game>> getNewGameList() {
        return newGameList;
    }

    public MutableLiveData<List<Game>> getRecentPlay() {
        return recentPlay;
    }

    public MutableLiveData<List<Game>> getEditorsChoiceList() {
        return editorsChoiceList;
    }

    public MutableLiveData<List<Game>> getHotGameList() {
        return hotGameList;
    }

    //----------------------------


    public void getNewGameListData() {
        List<Game> dumGameList = new ArrayList<>();
        dumGameList.add(new Game("1", "Game 1", "link"));
        dumGameList.add(new Game("2", "Game 2", "link"));
        dumGameList.add(new Game("3", "Game 3", "link"));
        dumGameList.add(new Game("4", "Game 4", "link"));
        newGameList.postValue(dumGameList);

        //phaseService.getNewGames().enqueue(new Callback<List<Game>>() {
        //    @Override
        //    public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
        //        newGameList.postValue(response.body());
        //    }
        //
        //    @Override
        //    public void onFailure(Call<List<Game>> call, Throwable t) {
        //        newGameList.postValue(null);
        //    }
        //});
    }

    public void getRecentPlayListData() {
        List<Game> dumGameList = new ArrayList<>();
        dumGameList.add(new Game("1", "Game 1", "link"));
        dumGameList.add(new Game("2", "Game 2", "link"));
        dumGameList.add(new Game("3", "Game 3", "link"));
        dumGameList.add(new Game("4", "Game 4", "link"));
        recentPlay.postValue(dumGameList);
    }

    public void getEditorsChoiceListData() {
        List<Game> dumGameList = new ArrayList<>();
        dumGameList.add(new Game("1", "Game 1", "link"));
        dumGameList.add(new Game("2", "Game 2", "link"));
        dumGameList.add(new Game("3", "Game 3", "link"));
        dumGameList.add(new Game("4", "Game 4", "link"));
        editorsChoiceList.postValue(dumGameList);
    }

    public void getHotGameListData() {
        List<Game> dumGameList = new ArrayList<>();
        dumGameList.add(new Game("1", "Game 1", "link"));
        dumGameList.add(new Game("2", "Game 2", "link"));
        dumGameList.add(new Game("3", "Game 3", "link"));
        dumGameList.add(new Game("4", "Game 4", "link"));
        hotGameList.postValue(dumGameList);
    }

}
