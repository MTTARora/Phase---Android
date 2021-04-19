package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rora.phase.model.Game;
import com.rora.phase.model.User;
import com.rora.phase.model.api.LoginCredential;
import com.rora.phase.model.api.SignUpCredential;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.utils.DataResponse;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;

    private LiveData<User> user;
    private LiveData<List<Game>> recentPlayList, favoriteList;
    private LiveData<DataResponse> signInResult;
    private LiveData<DataResponse> signUpResult;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application.getApplicationContext());

        user = userRepository.getUser();
        recentPlayList = userRepository.getRecentPlayList();
        favoriteList = userRepository.getFavoriteList();
        signInResult = userRepository.getSignInResult();
        signUpResult = userRepository.getSignUpResult();
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

    public LiveData<DataResponse> getSignInResult() {
        return signInResult;
    }

    public LiveData<DataResponse> getSignUpResult() {
        return signUpResult;
    }

    //----------------------------------------------

    public void signIn(String username, String password) {
        LoginCredential loginIdentify = new LoginCredential(username, password);
        userRepository.signIn(loginIdentify);
    }

    public void signInAsGuest() {
        userRepository.signInAsGuest();
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

    public boolean isUserLogged() {
        return userRepository.isUserLogged();
    }

    public User getLocalUser() {
        return new User(userRepository.getUserToken());
    }

    public void resetPlayData() {
        userRepository.storeCurrentGame(null);
    }

    public void signUp(String username, String password, String confirmPassword) {
        SignUpCredential signUpIdentify = new SignUpCredential(username, password, confirmPassword);
        userRepository.signUp(signUpIdentify);
    }
}
