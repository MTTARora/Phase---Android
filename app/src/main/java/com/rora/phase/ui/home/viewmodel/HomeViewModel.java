package com.rora.phase.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rora.phase.model.Game;
import com.rora.phase.repository.GameRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private GameRepository gameRepository;

    private LiveData<List<Game>> newGameList, recentPlayList, editorsChoiceList, hotGameList;

    public HomeViewModel() {
        gameRepository = new GameRepository();

        newGameList = gameRepository.getNewGameList();
        recentPlayList = gameRepository.getRecentPlayList();
        editorsChoiceList = gameRepository.getEditorsChoiceList();
        hotGameList = gameRepository.getHotGameList();
    }


    //---------- GET SET ----------

    public LiveData<List<Game>> getNewGameList() {
        return newGameList;
    }

    public LiveData<List<Game>> getRecentPlayList() {
        return recentPlayList;
    }

    public LiveData<List<Game>> getEditorsChoiceList() {
        return editorsChoiceList;
    }

    public LiveData<List<Game>> getHotGameList() {
        return hotGameList;
    }

    //----------------------------


    public void getNewGameListData() {
        gameRepository.getNewGameListData();
    }

    public void getRecentPlayData() {
        gameRepository.getRecentPlayListData();
    }

    public void getEditorsChoiceListData() {
        gameRepository.getEditorsChoiceListData();
    }

    public void getHotGameListData() {
        gameRepository.getHotGameListData();
    }

}