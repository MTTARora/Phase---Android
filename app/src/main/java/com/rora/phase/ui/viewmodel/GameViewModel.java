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
    //private MutableLiveData<ComputerDetails> computerDetails;

    public GameViewModel(Application application) {
        super(application);
        gameRepository = new GameRepository();

        game = gameRepository.getSelectedGame();
        //computerDetails = gameRepository.getComputer();
    }

    public LiveData<Game> getGameData() {
        return game;
    }

    //public LiveData<ComputerDetails> getComputerDetails() {
    //    return computerDetails;
    //}

    public void getGame(String gameId) {
        gameRepository.getGameData(gameId);
    }

    public boolean isUserLogged() {
        return !SharedPreferencesHelper.newInstance(getApplication().getBaseContext()).getUserToken().isEmpty();
    }
}
