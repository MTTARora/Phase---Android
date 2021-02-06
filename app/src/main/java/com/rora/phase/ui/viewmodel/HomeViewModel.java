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
import com.rora.phase.utils.PageManager;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private BannerRepository bannerRepository;

    private LiveData<List<Banner>> bannerList;
    private LiveData<List<Game>> newGameList, editorsChoiceList, hotGameList, trendingList, gameByCategoryList, recommendedList, gameByPayTypeList;
    private LiveData<List<Tag>> categoryList;
    private PageManager pager, newGamePager, editorPager, hotGamePager, trendingPager, gameByCategoryPager;

    public HomeViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application.getBaseContext());
        bannerRepository = new BannerRepository();
        gameRepository = new GameRepository();

        bannerList = bannerRepository.getBannerList();
        newGameList = gameRepository.getNewGameList();
        editorsChoiceList = gameRepository.getEditorsChoiceList();
        hotGameList = gameRepository.getHotGameList();
        trendingList = gameRepository.getTrendingList();
        categoryList = gameRepository.getCategoryList();
        gameByCategoryList = gameRepository.getGameByCategoryList();
        recommendedList = userRepository.getRecommendedGameList();
        gameByPayTypeList = gameRepository.getGamesByPayTypeList();
        pager = new PageManager();
        newGamePager = new PageManager();
        editorPager = new PageManager();
        hotGamePager = new PageManager();
        trendingPager = new PageManager();
        gameByCategoryPager = new PageManager();
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
        if (newGamePager.hasNext())
            gameRepository.getNewGameListData(newGamePager.nextPage(), newGamePager.getPageSize());
    }

    public void getRecentPlayData() {
        if (pager.hasNext())
            gameRepository.getRecentPlayListData(newGamePager.nextPage(), newGamePager.getPageSize());
    }

    public void getEditorsChoiceListData() {
        if (editorPager.hasNext())
            gameRepository.getEditorsChoiceListData(newGamePager.nextPage(), newGamePager.getPageSize());
    }

    public void getHotGameListData() {
        if (hotGamePager.hasNext())
            gameRepository.getHotGameListData(newGamePager.nextPage(), newGamePager.getPageSize());
    }

    public void getTrendingListData() {
        if (trendingPager.hasNext())
            gameRepository.getTrendingData(newGamePager.nextPage(), newGamePager.getPageSize());
    }

    public void getCategoryListData() {
        gameRepository.getCategoryListData();
    }

    public void getGamesByCategoryListData(String tagName) {
        if (pager.hasNext())
            gameRepository.getGamesByCategoryData(tagName, newGamePager.nextPage(), newGamePager.getPageSize());
    }

    public void getRecommendedGameListData() {
        if (pager.hasNext())
            userRepository.getRecommendedGameListData(newGamePager.nextPage(), newGamePager.getPageSize());
    }

    public void getGameByPayTypeListData(int payType) {
        if (pager.hasNext())
            gameRepository.getGamesByPayTypeData(payType, newGamePager.nextPage(), newGamePager.getPageSize());
    }

    public void refresh() {
        pager.reset();
        trendingPager.reset();
        hotGamePager.reset();
        editorPager.reset();
        newGamePager.reset();
        gameByCategoryPager.reset();
    }
}