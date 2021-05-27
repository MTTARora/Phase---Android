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

import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends AndroidViewModel {

    private GameRepository gameRepository;
    private MutableLiveData<Game> game;
    private MutableLiveData<Game> currentGame;
    private MutableLiveData<List<Game>> similarGameList, newGameList, hotGameList;

    public GameViewModel(Application application) {
        super(application);
        gameRepository = new GameRepository();

        game = new MutableLiveData<>();
        similarGameList = new MutableLiveData<>();
        newGameList = new MutableLiveData<>();
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
        game.setValue(null);
        similarGameList.setValue(null);
    }

    public void getGame(String gameId) {
        gameRepository.getGameData(gameId, (errMsg, data) -> {
            if (errMsg != null) {
                game.setValue(null);
                return;
            }

            game.setValue(data);
        });
    }

    public boolean isUserLogged() {
        return !SharedPreferencesHelper.newInstance(getApplication().getBaseContext()).getUserToken().isEmpty();
    }

    public void getNewGameListData(int page, int pageSize) {
        gameRepository.getNewGameListData(page, pageSize, (errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty())
                newGameList.setValue(new ArrayList<>());
            else
                newGameList.setValue(data);
        });
    }

    public void getSimilarGameList(String gameId) {
        gameRepository.getSimilarGameListData(gameId, 1, 20, (errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty())
                similarGameList.setValue(new ArrayList<>());
            else
                similarGameList.setValue(data);
        });
    }

    public void getHotGameListData(int page, int pageSize) {
        gameRepository.getHotGameListData(page, pageSize);
    }

}
