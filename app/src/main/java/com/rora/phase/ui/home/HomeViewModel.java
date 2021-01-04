package com.rora.phase.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rora.phase.Game;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Game>> newGame;

    public HomeViewModel() {
        newGame = new MutableLiveData<>();
    }

    public LiveData<List<Game>> getNewGame() {
        return newGame;
    }

}