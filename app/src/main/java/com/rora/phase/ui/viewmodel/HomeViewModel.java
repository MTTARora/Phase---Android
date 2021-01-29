package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rora.phase.model.Banner;
import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.repository.BannerRepository;
import com.rora.phase.repository.GameRepository;
import com.rora.phase.repository.UserRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private BannerRepository bannerRepository;

    private LiveData<List<Banner>> bannerList;
    private LiveData<List<Game>> newGameList, recentPlayList, editorsChoiceList, hotGameList, trendingList, gameByCategoryList, recommendedList, gameByPayTypeList;
    private LiveData<List<Tag>> categoryList;

    public HomeViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application.getBaseContext());
        bannerRepository = new BannerRepository();
        gameRepository = new GameRepository();

        bannerList = bannerRepository.getBannerList();
        newGameList = gameRepository.getNewGameList();
        recentPlayList = gameRepository.getRecentPlayList();
        editorsChoiceList = gameRepository.getEditorsChoiceList();
        hotGameList = gameRepository.getHotGameList();
        trendingList = gameRepository.getTrendingList();
        categoryList = gameRepository.getCategoryList();
        gameByCategoryList = gameRepository.getGameByCategoryList();
        recommendedList = userRepository.getRecommendedGameList();
        gameByPayTypeList = gameRepository.getGamesByPayTypeList();
    }


    //---------- GET SET ----------

    public LiveData<List<Banner>> getBannerList() {
        return bannerList;
    }

    public LiveData<List<Game>> getNewGameList() {
        return newGameList;
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

    public LiveData<List<Game>> getGameByCategoryList() {
        return gameByCategoryList;
    }

    public LiveData<List<Tag>> getCategoryList() {
        return categoryList;
    }

    public LiveData<List<Game>> getRecentPlayList() {
        return recentPlayList;
    }

    public LiveData<List<Game>> getRecommendedGameList() {
        return recommendedList;
    }

    public LiveData<List<Game>> getGamesByPayTypeList() {
        return gameByPayTypeList;
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

    public void getCategoryListData() {
        gameRepository.getCategoryListData();
    }

    public void getGamesByCategoryListData(String tagName) {
        gameRepository.getGamesByCategoryData(tagName);
    }

    public void getRecommendedGameListData() {
        userRepository.getRecommendedGameListData();
    }

    public void getGameByPayTypeListData(int payType) {
        gameRepository.getGamesByPayTypeData(payType);
    }

}