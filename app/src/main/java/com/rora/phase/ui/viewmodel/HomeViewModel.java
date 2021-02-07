package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rora.phase.model.Banner;
import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.repository.BannerRepository;
import com.rora.phase.repository.GameRepository;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.utils.PageManager;

import java.util.List;

import javax.annotation.Nullable;

public class HomeViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private BannerRepository bannerRepository;

    private LiveData<List<Banner>> bannerList;
    private LiveData<List<Game>> newGameList, editorsChoiceList, hotGameList, trendingList, gameByCategoryList, recommendedList, gameByPayTypeList;
    private LiveData<List<Tag>> categoryList;
    private LiveData<Game> selectedGame;
    private PageManager pager, newGamePager, editorPager, hotGamePager, trendingPager, gameByCategoryPager;
    private String currentSelectedItemId;

    public enum GameListType {
        NEW,
        HOT,
        EDITOR,
        TRENDING,
        BY_CATEGORY,
        BY_PAY_TYPE,
        RECOMMENDED
    }

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
        selectedGame = gameRepository.getSelectedGame();

        pager = new PageManager();
        newGamePager = new PageManager();
        editorPager = new PageManager();
        hotGamePager = new PageManager();
        trendingPager = new PageManager();
        gameByCategoryPager = new PageManager();
        currentSelectedItemId = "";
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

    public String getCurrentSelectedItemId() {
        return currentSelectedItemId;
    }

    public LiveData<Game> getSelectedGame() {
        return selectedGame;
    }

    //----------------------------

    public void getBannerListData() {
        bannerRepository.getBannerListData();
    }

    public void getCategoryListData() {
        gameRepository.getCategoryListData();
    }

    private void getNewGameListData(int page, int pageSize) {
        gameRepository.getNewGameListData(page, pageSize);
    }

    private void getEditorsChoiceListData(int page, int pageSize) {
        gameRepository.getEditorsChoiceListData(page, pageSize);
    }

    private void getHotGameListData(int page, int pageSize) {
        gameRepository.getHotGameListData(page, pageSize);
    }

    private void getTrendingListData(int page, int pageSize) {
        gameRepository.getTrendingData(page, pageSize);
    }

    private void getGamesByCategoryListData(String tagName, int page, int pageSize) {
        currentSelectedItemId = tagName;
        gameRepository.getGamesByCategoryData(tagName, page, pageSize);
    }

    private void getRecommendedGameListData(int page, int pageSize) {
        userRepository.getRecommendedGameListData(page, pageSize);
    }

    private void getGameByPayTypeListData(String payType, int page, int pageSize) {
        currentSelectedItemId = payType;
        gameRepository.getGamesByPayTypeData(payType, page, pageSize);
    }

    public void getGame(String game) {
        gameRepository.getGameData(game);
    }

    /** This method will always get the first page with default page size
     * @param param category id, pay type,...
     * */
    public void getGamesByType(GameListType type, @Nullable String param) {
        refresh(type);
        if (type != null)
            switch (type) {
                case NEW:
                    getNewGameListData(1, newGamePager.getPageSize());
                    break;
                case HOT:
                    getHotGameListData(1, hotGamePager.getPageSize());
                    break;
                case EDITOR:
                    getEditorsChoiceListData(1, editorPager.getPageSize());
                    break;
                case RECOMMENDED:
                    getRecommendedGameListData(1, pager.getPageSize());
                    break;
                case TRENDING:
                    getTrendingListData(1, trendingPager.getPageSize());
                    break;
                case BY_CATEGORY:
                    getGamesByCategoryListData(param, 1, pager.getPageSize());
                    break;
                case BY_PAY_TYPE:
                    getGameByPayTypeListData(param, 1, pager.getPageSize());
                    break;
                default: break;
            }
    }

    public void loadMore(GameListType type, String param) {
        if (type != null)
            switch (type) {
                case NEW:
                    if (newGamePager.hasNext())
                        getNewGameListData(newGamePager.nextPage(), newGamePager.getPageSize());
                    break;
                case HOT:
                    if (hotGamePager.hasNext())
                        getHotGameListData(hotGamePager.nextPage(), hotGamePager.getPageSize());
                    break;
                case EDITOR:
                    if (editorPager.hasNext())
                        getEditorsChoiceListData(editorPager.nextPage(), editorPager.getPageSize());
                    break;
                case TRENDING:
                    if (trendingPager.hasNext())
                        getTrendingListData(trendingPager.nextPage(), trendingPager.getPageSize());
                    break;
                case BY_CATEGORY:
                    if (gameByCategoryPager.hasNext())
                        getGamesByCategoryListData(param,  gameByCategoryPager.nextPage(), gameByCategoryPager.getPageSize());
                    break;
                case BY_PAY_TYPE:
                    if (pager.hasNext())
                        getGameByPayTypeListData(param, pager.nextPage(), pager.getPageSize());
                    break;
                case RECOMMENDED:
                    if (pager.hasNext())
                        getRecommendedGameListData(pager.nextPage(), pager.getPageSize());
                    break;
                default: break;
            }
    }

    /** @param type null to reset all */
    public void refresh(GameListType type) {
        if (type == null) {
            pager.reset();
            newGamePager.reset();
            hotGamePager.reset();
            trendingPager.reset();
            editorPager.reset();
            gameByCategoryPager.reset();
            //gameRepository.reset();
        } else
            switch (type) {
                case NEW:
                    newGamePager.reset();
                    break;
                case HOT:
                    hotGamePager.reset();
                    break;
                case TRENDING:
                    trendingPager.reset();
                    break;
                case EDITOR:
                    editorPager.reset();
                    break;
                case RECOMMENDED:
                    pager.reset();
                case BY_PAY_TYPE:
                    pager.reset();
                    break;
                case BY_CATEGORY:
                    gameByCategoryPager.reset();
                    break;
                default:
                    pager.reset();
                    newGamePager.reset();
                    hotGamePager.reset();
                    trendingPager.reset();
                    editorPager.reset();
                    gameByCategoryPager.reset();
                    //gameRepository.reset();
                    break;
        }
    }

}