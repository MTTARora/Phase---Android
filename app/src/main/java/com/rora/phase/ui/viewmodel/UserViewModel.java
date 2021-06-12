package com.rora.phase.ui.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.model.Transaction;
import com.rora.phase.model.User;
import com.rora.phase.model.Wallet;
import com.rora.phase.model.api.DepositData;
import com.rora.phase.model.api.LoginCredential;
import com.rora.phase.model.api.SignUpCredential;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.utils.DataResult;
import com.rora.phase.utils.callback.OnResultCallBack;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private Context context;
    private UserRepository userRepository;

    private LiveData<User> user;
    private MutableLiveData<List<Game>> favoriteList;
    private LiveData<DataResult> signInResult;
    private LiveData<DataResult> signUpResult;
    private MutableLiveData<DataResult> forgotPasswordResult;
    private MutableLiveData<DataResult> emailVerificationResult;
    private MutableLiveData<DataResult<Game>> updateFavoriteResult;
    private MutableLiveData<Game> currentRecentPlay;
    private MutableLiveData<List<Game>> recentPlayList;
    private MutableLiveData<Boolean> triggerLoginListener;
    private MutableLiveData<DataResult<Wallet>> walletResult;
    private MutableLiveData<DataResult<List<Transaction>>> transactionsResult;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
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
        walletResult = new MutableLiveData<>();
        transactionsResult = new MutableLiveData<>();
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

    public LiveData<DataResult> getSignInResult() {
        return signInResult;
    }

    public LiveData<DataResult> getSignUpResult() {
        return signUpResult;
    }

    public LiveData<DataResult> getForgotPasswordResult() {
        return forgotPasswordResult;
    }

    public LiveData<DataResult> getEmailVerificationResult() {
        return emailVerificationResult;
    }

    public LiveData<Game> getCurrentRecentPlay() {
        return currentRecentPlay;
    }

    public LiveData<Boolean> triggerLoginListener() {
        return triggerLoginListener;
    }

    public MutableLiveData<DataResult<Game>> getUpdateFavoriteResult() {
        return updateFavoriteResult;
    }

    public MutableLiveData<DataResult<Wallet>> getWalletResult() {
        return walletResult;
    }

    public MutableLiveData<DataResult<List<Transaction>>> getTransactionsResult() {
        return transactionsResult;
    }

    //----------------------------------------------

    public void signIn(String username, String password) {
        LoginCredential loginIdentify = new LoginCredential(username, password);
        userRepository.signIn(loginIdentify);
    }

    public void signInAsGuest() {
        userRepository.signInAsGuest();
    }

    public void signOut() {
        userRepository.signOut();
    }

    public void signUp(String username, String password, String confirmPassword) {
        SignUpCredential signUpIdentify = new SignUpCredential(username, password, confirmPassword);
        userRepository.signUp(signUpIdentify);
    }

    public void forgotPassword(String email) {
        userRepository.forgotPassword(email, (errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty())
                forgotPasswordResult.setValue(new DataResult(errMsg, null));
            else
                forgotPasswordResult.setValue(new DataResult(null, data));
        });
    }

    public void verifyEmail(String email) {
        userRepository.verifyEmail(email, (errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty())
                forgotPasswordResult.setValue(new DataResult(errMsg, null));
            else
                forgotPasswordResult.setValue(new DataResult(null, data));
        });
    }

    public void getRecentPlayData() {
        userRepository.getRecentPlayData((errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty()) {
                recentPlayList.setValue(new ArrayList<>());
                if (errMsg.equals("401"))
                    triggerLogin();
            }
            else
                recentPlayList.setValue(data);
        });
    }

    public void getFavoriteListData() {
        userRepository.getFavoriteListData((errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty()) {
                favoriteList.setValue(null);
                if (errMsg.equals("401"))
                    triggerLogin();
            }
            else
                favoriteList.setValue(data);
        });
    }

    public void updateFavorite(Game game, OnResultCallBack onResultCallBack) {
        DataResult<Game> result = new DataResult<>();

        if (game.getFavorited()) {
            userRepository.removeFavorite(game.getId().toString(), (errMsg, data) -> {
                if (errMsg != null && !errMsg.isEmpty()) {
                    onResultCallBack.onResult(errMsg, null);
                    result.setMsg(errMsg);
                    if (errMsg.equals("401"))
                        triggerLogin();
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
                    if (errMsg.equals("401"))
                        triggerLogin();
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

    public User getLocalUserInfo() {
        if (userRepository.getLocalUserInfo().isEmpty())
            return null;

        return (new Gson()).fromJson(userRepository.getLocalUserInfo(), User.class);
    }

    public void resetPlayData() {
        userRepository.storeCurrentGame(null);
    }

    public void setCurrentRecentPlay(Game game) {
        currentRecentPlay.postValue(game);
    }

    public void triggerLogin() {
        triggerLoginListener.setValue(true);
        userRepository.signOut();
    }

    public void getWallet() {
        userRepository.getWallet((err, data) -> {
            DataResult<Wallet> result = new DataResult<>(err, data);
            if (err != null && !err.isEmpty()) {
                if (err.equals("401"))
                    triggerLogin();
            }

            walletResult.setValue(result);
        });
    }

    public void getTransactions() {
        userRepository.getTransactions((err, transactions) -> {
            DataResult<List<Transaction>> result = new DataResult<>(err, transactions);
            if (err != null && !err.isEmpty()) {
                if (err.equals("401"))
                    triggerLogin();
            }

            transactionsResult.setValue(result);
        });
    }

    public void deposit(String amount, int productId, int type, OnResultCallBack<String> onResultCallBack) {
        double amountD = Double.parseDouble(amount);

        if (amountD == 0) {
            onResultCallBack.onResult(context.getResources().getString(R.string.wrong_info_msg), null);
            return;
        }

        userRepository.deposit(new DepositData(productId, amountD, type), (errMsg, data) -> {
            if (errMsg != null && !errMsg.isEmpty())
                onResultCallBack.onResult(errMsg, null);
            else
                onResultCallBack.onResult(null, data);
        });
    }
}
