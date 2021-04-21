package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.repository.GameRepository;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.utils.SharedPreferencesHelper;

public class GameViewModel extends AndroidViewModel {

    private GameRepository gameRepository;
    private MutableLiveData<Game> game;
    private MutableLiveData<Game> currentGame;

    public GameViewModel(Application application) {
        super(application);
        gameRepository = new GameRepository();

        game = gameRepository.getSelectedGame();
        currentGame = new MutableLiveData<>();
    }

    public LiveData<Game> getGameData() {
        return game;
    }

    public MutableLiveData<Game> getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        currentGame.postValue(game);
    }

    public void getGame(String gameId) {
        gameRepository.getGameData(gameId);
    }

    public boolean isUserLogged() {
        return !SharedPreferencesHelper.newInstance(getApplication().getBaseContext()).getUserToken().isEmpty();
    }
}
