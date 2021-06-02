package com.rora.phase.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.model.User;
import com.rora.phase.model.api.LoginCredential;
import com.rora.phase.model.api.SignUpCredential;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.utils.DataResponse;
import com.rora.phase.utils.callback.OnResultCallBack;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;

    private LiveData<User> user;
    private MutableLiveData<List<Game>> favoriteList;
    private LiveData<DataResponse> signInResult;
    private LiveData<DataResponse> signUpResult;
    private MutableLiveData<DataResponse> forgotPasswordResult;
    private MutableLiveData<DataResponse> emailVerificationResult;
    private MutableLiveData<DataResponse<Game>> updateFavoriteResult;
    private MutableLiveData<Game> currentRecentPlay;
    private MutableLiveData<List<Game>> recentPlayList;
    private MutableLiveData<Boolean> triggerLoginListener;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application.getApplicationContext());

        user = userRepository.getUser();
        recentPlayList = new MutableLiveData<>();
        favoriteList = new MutableLiveData<>();
        signInResult = userRepository.getSignInResult();
        signUpResult = userRepository.getSignUpResult();
        forgotPasswordResult = new MutableLiveData<>();
        emailVerificationResult = new MutableLiveData<>();
        currentRecentPlay = new MutableLiveData<>();
        triggerLoginListener = new MutableLiveData<>();
        updateFavoriteResult = new MutableLiveData<>();
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

    public LiveData<DataResponse> getForgotPasswordResult() {
        return forgotPasswordResult;
    }

    public LiveData<DataResponse> getEmailVerificationResult() {
        return emailVerificationResult;
    }

    public LiveData<Game> getCurrentRecentPlay() {
        return currentRecentPlay;
    }

    public LiveData<Boolean> triggerLoginListener() {
        return triggerLoginListener;
    }

    public MutableLiveData<DataResponse<Game>> getUpdateFavoriteResult() {
        return updateFavoriteResult;
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
        userRepository.getRecentPlayData((errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty())
                recentPlayList.setValue(new ArrayList<>());
            else
                recentPlayList.setValue(data);
        });
    }

    public void getFavoriteListData() {
        userRepository.getFavoriteListData((errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty())
                favoriteList.setValue(null);
            else
                favoriteList.setValue(data);
        });
    }

    public void updateFavorite(Game game, OnResultCallBack onResultCallBack) {
        DataResponse<Game> result = new DataResponse<>();

        if (game.getFavorited()) {
            userRepository.removeFavorite(game.getId().toString(), (errMsg, data) -> {
                if (errMsg != null && !errMsg.isEmpty()) {
                    onResultCallBack.onResult(errMsg, null);
                    result.setMsg(errMsg);
                } else {
                    game.setFavorited(false);
                    onResultCallBack.onResult(null, null);
                    result.setData(game);
                }
                updateFavoriteResult.setValue(result);
            });
        } else {
            userRepository.addFavorite(game.getId().toString(), (errMsg, data) -> {
                if (errMsg != null && !errMsg.isEmpty()) {
                    onResultCallBack.onResult(errMsg, null);
                    result.setMsg(errMsg);
                } else {
                    game.setFavorited(true);
                    onResultCallBack.onResult(null, null);
                    result.setData(game);
                }
                updateFavoriteResult.setValue(result);
            });
        }
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

    public void forgotPassword(String email) {
        userRepository.forgotPassword(email, (errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty())
                forgotPasswordResult.setValue(new DataResponse(errMsg, null));
            else
                forgotPasswordResult.setValue(new DataResponse(null, data));
        });
    }

    public void verifyEmail(String email) {
        userRepository.verifyEmail(email, (errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty())
                forgotPasswordResult.setValue(new DataResponse(errMsg, null));
            else
                forgotPasswordResult.setValue(new DataResponse(null, data));
        });
    }

    public void setCurrentRecentPlay(Game game) {
        currentRecentPlay.postValue(game);
    }

    public void triggerLogin() {
        triggerLoginListener.setValue(true);
        userRepository.signOut();
    }
}
