package com.rora.phase.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rora.phase.model.Banner;
import com.rora.phase.model.Game;
import com.rora.phase.repository.BannerRepository;
import com.rora.phase.repository.GameRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private GameRepository gameRepository;
    private BannerRepository bannerRepository;

    private LiveData<List<Banner>> bannerList;
    private LiveData<List<Game>> newGameList, recentPlayList, editorsChoiceList, hotGameList, trendingList;

    public HomeViewModel() {
        bannerRepository = new BannerRepository();
        gameRepository = new GameRepository();

        bannerList = bannerRepository.getBannerList();
        newGameList = gameRepository.getNewGameList();
        recentPlayList = gameRepository.getRecentPlayList();
        editorsChoiceList = gameRepository.getEditorsChoiceList();
        hotGameList = gameRepository.getHotGameList();
        trendingList = gameRepository.getTrendingList();
    }


    //---------- GET SET ----------

    public LiveData<List<Banner>> getBannerList() {
        return bannerList;
    }

    public LiveData<List<Game>> getNewGameList() {
        return newGameList;
    }

    public LiveData<List<Game>> getRecentPlayList() {
        return recentPlayList;
    }

    public LiveData<List<Game>> getEditorChoiceList() {
        return editorsChoiceList;
    }

    public LiveData<List<Game>> getHotGameList() {
        return hotGameList;
    }

    public LiveData<List<Game>> getTrendingList() {
        return trendingList;
    }

    //----------------------------


    public void getBannerListData() {
        bannerRepository.getBannerListData();
    }

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

    public void getTrendingListData() {
        gameRepository.getTrendingData();
    }

}