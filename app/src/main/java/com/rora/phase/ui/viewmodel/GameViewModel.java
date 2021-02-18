package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.repository.GameRepository;

public class GameViewModel extends AndroidViewModel {

    private GameRepository gameRepository;
    private MutableLiveData<Game> game;
    private MutableLiveData<ComputerDetails> computerDetails;


    //---------------------- BASIC METHODS -----------------------------

    public GameViewModel(Application application) {
        super(application);
        gameRepository = new GameRepository();

        game = gameRepository.getSelectedGame();
        computerDetails = gameRepository.getComputer();
    }

    public LiveData<Game> getGameData() {
        return game;
    }

    public LiveData<ComputerDetails> getComputerDetails() {
        return computerDetails;
    }

    //------------------------------------------------------------------

    public void getGame(String gameId) {
        gameRepository.getGameData(gameId);
    }

    //====================== PLAYING GAME STEPS =========================

    /** STEP 1: Get computer ip from server
     *
     * STEP 2: Create computer data and handle in local
     *
     * STEP 3: Pair
     */

    public void getComputerDetailsData() {

        //STEP 1
        gameRepository.getComputerIPData();
    }

    /** STEP 2: Connect to pc
     *
     * Send paring pin to server
     *
     * */


    /** STEP 3: Start calculating playtime
     *
     *
     *
     * */

    public void startCalculatingPlaytime() {

    }


    //--------SUPPORT FUNCTIONS FOR PLAYING--------


    //--------------------------------------------

    //===================================================================

}
