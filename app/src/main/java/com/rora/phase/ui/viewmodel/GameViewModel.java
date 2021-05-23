package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.repository.GameRepository;
import com.rora.phase.utils.SharedPreferencesHelper;
import com.rora.phase.utils.callback.OnResultCallBack;

import java.util.List;

public class GameViewModel extends AndroidViewModel {

    private GameRepository gameRepository;
    private MutableLiveData<Game> game;
    private MutableLiveData<Game> currentGame;
    private LiveData<List<Game>> similarGameList, newGameList, hotGameList;

    public GameViewModel(Application application) {
        super(application);
        gameRepository = new GameRepository();

        game = new MutableLiveData<>();
        similarGameList = gameRepository.getSimilarGameList();
        newGameList = gameRepository.getNewGameList();
        hotGameList = gameRepository.getHotGameList();
        currentGame = new MutableLiveData<>();
    }

    public LiveData<Game> getGameData() {
        return game;
    }

    public LiveData<List<Game>> getNewGameList() {
        return newGameList;
    }

    public LiveData<List<Game>> getHotGameList() {
        return hotGameList;
    }

    public LiveData<List<Game>> getSimilarGames() {
        return similarGameList;
    }

    public MutableLiveData<Game> getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        currentGame.postValue(game);
    }


    //SERVICES

    public void resetGameData() {
        game.postValue(null);
    }

    public void getGame(String gameId) {
        gameRepository.getGameData(gameId, (errMsg, data) -> {
            if (errMsg != null) {
                game.postValue(null);
                return;
            }

            game.postValue(data);
        });
    }

    public boolean isUserLogged() {
        return !SharedPreferencesHelper.newInstance(getApplication().getBaseContext()).getUserToken().isEmpty();
    }

    public void getNewGameListData(int page, int pageSize) {
        gameRepository.getNewGameListData(page, pageSize);
    }

    public void getSimilarGameList(String gameId) {
        gameRepository.getSimilarGameListData(gameId, 1, 20);
    }

    public void getHotGameListData(int page, int pageSize) {
        gameRepository.getHotGameListData(page, pageSize);
    }
}
