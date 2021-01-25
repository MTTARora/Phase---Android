package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rora.phase.model.Game;
import com.rora.phase.model.User;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.utils.DataResultHelper;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;

    private LiveData<User> user;
    private LiveData<List<Game>> recentPlayList, favoriteList;
    private LiveData<DataResultHelper> updatingDataResult;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application.getApplicationContext());

        recentPlayList = userRepository.getRecentPlayList();
        favoriteList = userRepository.getFavoriteList();
        updatingDataResult = userRepository.getUpdatingDataResult();
    }

    //-------------------GET/SET--------------------

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<List<Game>> getRecentPlayList() {
        return recentPlayList;
    }

    public LiveData<List<Game>> getFavoriteList() {
        return favoriteList;
    }

    public LiveData<DataResultHelper> getUpdatingDataResult() {
        return updatingDataResult;
    }

    //----------------------------------------------


    public void getUserData() {
        userRepository.getUserInfo();
    }

    public void getRecentPlayData() {
        userRepository.getRecentPlayData();
    }

    public void getFavoriteListData() {
        userRepository.getFavoriteListData();
    }

    public void addFavorite(String gameId) {
        userRepository.addFavorite(gameId);
    }

    public void removeFavorite(String gameId) {
        userRepository.removeFavorite(gameId);
    }

}
