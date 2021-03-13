package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.repository.GameRepository;
import com.rora.phase.repository.UserRepository;

public class GameViewModel extends AndroidViewModel {

    private GameRepository gameRepository;
    private UserRepository userRepository;
    private MutableLiveData<Game> game;
    //private MutableLiveData<ComputerDetails> computerDetails;

    public GameViewModel(Application application) {
        super(application);
        gameRepository = new GameRepository();
        userRepository = new UserRepository(application.getBaseContext());

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

    //public void getComputerDetailsData() {
    //    //if (userRepository.isUserLogged())
    //    gameRepository.getComputerIPData();
    //    //else
    //    //    getApplication().getResources().getString(R.string.require_login_msg);
    //}

    public boolean isStopPlaying() {
        return userRepository.isStopPlaying();
    }

    public boolean isUserLogged() {
        return userRepository.isUserLogged();
    }
}
