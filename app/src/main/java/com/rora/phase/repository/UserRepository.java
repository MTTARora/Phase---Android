package com.rora.phase.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.rora.phase.model.Game;
import com.rora.phase.model.Transaction;
import com.rora.phase.model.User;
import com.rora.phase.model.UserPlayingData;
import com.rora.phase.model.Wallet;
import com.rora.phase.model.api.DepositData;
import com.rora.phase.model.api.FindingHostResponse;
import com.rora.phase.model.api.LoginCredential;
import com.rora.phase.model.api.LoginResponse;
import com.rora.phase.model.api.PinConfirmData;
import com.rora.phase.model.api.PrepareAppModel;
import com.rora.phase.model.api.SignUpCredential;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.utils.DataResult;
import com.rora.phase.utils.SharedPreferencesHelper;
import com.rora.phase.utils.callback.OnResultCallBack;
import com.rora.phase.utils.network.APIServicesHelper;
import com.rora.phase.utils.network.BaseResponse;
import com.rora.phase.utils.network.PhaseServiceHelper;
import com.rora.phase.utils.network.UserPhaseService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private UserPhaseService userAuthenticatedServices;
    private UserPhaseService userServices;
    private UserPhaseService walletServices;
    private SharedPreferencesHelper dbSharedPref;

    private MutableLiveData<User> user;
    private MutableLiveData<DataResult> signInResult;
    private MutableLiveData<DataResult> signUpResult;

    public static UserRepository newInstance(Context context) {
        return new UserRepository(context);
    }

    public UserRepository(Context context) {
        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper(context);
        userAuthenticatedServices = phaseServiceHelper.getUserPhaseService(true);
        userServices = phaseServiceHelper.getUserPhaseService(false);
        walletServices = phaseServiceHelper.getWalletService();
        dbSharedPref = new SharedPreferencesHelper(context);

        user = new MutableLiveData<>();
        signInResult = new MutableLiveData<>();
        signUpResult = new MutableLiveData<>();
    }

    //--------------------------------GET/SET------------------------

    public MutableLiveData<User> getUser() {
        return user;
    }

    public MutableLiveData<DataResult> getSignInResult() {
        return signInResult;
    }

    public MutableLiveData<DataResult> getSignUpResult() {
        return signUpResult;
    }

    //---------------------------------------------------------------


    //--------------------------------- NETWORK SERVICES -----------------------------------

    public void signUp(SignUpCredential credential) {
        APIServicesHelper apiHelper = new APIServicesHelper<>();

        apiHelper.request(userServices.signUp(credential), (err, data) -> {
            if (err != null)
                signUpResult.setValue(new DataResult(err, null));
            else
                signUpResult.setValue(new DataResult(null, data));
        });
    }

    public void signIn(LoginCredential loginCredential) {
        APIServicesHelper<LoginResponse> apiHelper = new APIServicesHelper<>();

        apiHelper.request(userServices.signIn(loginCredential), (err, data) -> {
            if (err != null) {
                signInResult.setValue(new DataResult(err, null));
            } else {
                User user = data.getInfo();
                String token = data.getToken();

                if (token != null && user != null)
                    storeLocalUser((new Gson()).toJson(user), token);

                signInResult.setValue(new DataResult(null, data));
            }
        });
    }

    public void signInAsGuest() {
        signInResult.postValue(new DataResult<String>(null, null));
    }

    public void signOut() {
        storeLocalUser(null, null);
    }

    public void forgotPassword(String email, OnResultCallBack resultCallBack) {
        APIServicesHelper apiHelper = new APIServicesHelper<>();

        apiHelper.request(userServices.forgotPassword(new User(email)), (err, data) -> {
            if (err != null)
                resultCallBack.onResult(err, null);
            else
                resultCallBack.onResult(null, data);
        });
    }

    public void verifyEmail(String email, OnResultCallBack callBack) {
        APIServicesHelper apiHelper = new APIServicesHelper<>();

        apiHelper.request(userServices.verifyEmail(email), (err, data) -> {
            if (err != null)
                callBack.onResult(err, null);
            else
                callBack.onResult(null, data);
        });
    }

    public void updateUser(User user) {
        userAuthenticatedServices.updateInfo(user).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                signInResult.postValue(new DataResult());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                signInResult.postValue(new DataResult("Please try again later!", null));
            }
        });
    }

    public void getFavoriteListData(OnResultCallBack<List<Game>> resultCallBack) {
        APIServicesHelper<List<Game>> apiServicesHelper = new APIServicesHelper<>();

        apiServicesHelper.request(userAuthenticatedServices.getFavorites(), (err, data) -> {
            if (err != null)
                resultCallBack.onResult(err, null);
            else
                resultCallBack.onResult(null, data == null ? new ArrayList<>() : data);
        });
    }

    public void addFavorite(String gameId, OnResultCallBack resultCallBack) {
        APIServicesHelper apiServicesHelper = new APIServicesHelper<>();

        apiServicesHelper.request(userAuthenticatedServices.addFavorite(gameId), (err, data) -> {
            if (err != null)
                resultCallBack.onResult(err, null);
            else
                resultCallBack.onResult(null, data);
        });
    }

    public void removeFavorite(String gameId, OnResultCallBack resultCallBack) {
        APIServicesHelper servicesHelper = new APIServicesHelper();

        servicesHelper.request(userAuthenticatedServices.removeFavorite(gameId), (err, data) -> {
            if (err != null)
                resultCallBack.onResult(err, null);
            else
                resultCallBack.onResult(null, data);
        });
    }

    public void getRecentPlayData(OnResultCallBack<List<Game>> onResultCallBack) {
        APIServicesHelper<List<Game>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(userAuthenticatedServices.getRecentPlay(1, 20), (err, data) -> {
            if (err != null)
                onResultCallBack.onResult(err, null);
            else
                onResultCallBack.onResult(null, data == null ? new ArrayList<>() : data);
        });
    }

    public void getAvailableHost(String deviceName, String deviceId, String gameId, OnResultCallBack<FindingHostResponse> callBack) {
        APIServicesHelper<FindingHostResponse> apiHelper = new APIServicesHelper<>();

        apiHelper.request(userAuthenticatedServices.getAvailableHost(deviceName, deviceId, gameId), (err, data) -> {
            if (err != null)
                callBack.onResult(err, null);
            else
                callBack.onResult(null, data);
        });
    }

    public void sendPinToHost(String pinStr, OnResultCallBack<ComputerDetails> callBack) {
        APIServicesHelper apiHelper = new APIServicesHelper<>();

        apiHelper.request(userAuthenticatedServices.sendPinToHost(new PinConfirmData(pinStr)), (err, data) -> {
            if (err != null)
                callBack.onResult(err, null);
            else
                callBack.onResult(null, null);
        });
    }

    public void prepareAppHost(String gameId, String platformId, String platformUsername, String platformPassword, OnResultCallBack<String> callBack) {
        APIServicesHelper apiHelper = new APIServicesHelper<>();
        PrepareAppModel data = new PrepareAppModel(gameId, platformId, platformUsername, platformPassword);
        apiHelper.request(userAuthenticatedServices.prepareApp(data), (err, result) -> {
            if (err != null && !err.contains("success"))
                callBack.onResult(err, null);
            else
                callBack.onResult(null, null);
        });
    }

    public void getWallet(OnResultCallBack<Wallet> onResultCallBack) {
        APIServicesHelper<Wallet> apiServicesHelper = new APIServicesHelper<>();

        apiServicesHelper.request(walletServices.getWallet(), (err, result) -> {
            if (err != null && !err.isEmpty())
                onResultCallBack.onResult(err, null);
            else
                onResultCallBack.onResult(null, result);
        });
    }

    public void getTransactions(OnResultCallBack<List<Transaction>> resultCallBack) {
        APIServicesHelper<List<Transaction>> apiServicesHelper = new APIServicesHelper<>();

        apiServicesHelper.request(walletServices.getTransactions(), (err, result) -> {
            if (err != null && !err.isEmpty())
                resultCallBack.onResult(err, null);
            else
                resultCallBack.onResult(null, result);
        });
    }

    public void deposit(DepositData data, OnResultCallBack<String> onResultCallBack) {
        APIServicesHelper<String> apiServicesHelper = new APIServicesHelper<>();

        apiServicesHelper.request(walletServices.deposit(data), (err, result) -> {
            if (err != null && !err.isEmpty())
                onResultCallBack.onResult(err, null);
            else
                onResultCallBack.onResult(null, result);
        });

        //onResultCallBack.onResult(null, "https://developer.android.com/guide/webapps/webview");
    }

    //----------------------------------------------------------------------------------------


    //------------------------------------ LOCAL SERVICES ------------------------------------

    public String getUserToken() {
        return dbSharedPref.getUserToken();
    }

    public void storeLocalUser(String userInfo, String token) {
        dbSharedPref.saveUserInfo(userInfo);
        dbSharedPref.setUserToken(token);
    }

    public String getLocalUserInfo() {
        return dbSharedPref.getUserInfo();
    }

    public boolean isUserLogged() {
        return !dbSharedPref.getUserToken().isEmpty();
    }

    public void saveLocalUserComputer(ComputerDetails data) {
        dbSharedPref.saveUserComputer((new Gson()).toJson(data));
    }

    public ComputerDetails getLocalUserComputer() {
        String rawData = dbSharedPref.getUserComputer();
        if (rawData.isEmpty())
            return null;

        return (new Gson()).fromJson(rawData, ComputerDetails.class);
    }

    public boolean isStopPlaying() {
        return dbSharedPref.getUserPlayState().equals(UserPlayingData.PlayingState.STOPPED.id);
    }

    public void savePlayState(UserPlayingData.PlayingState state) {
        dbSharedPref.saveUserPlayState(state.id);
    }

    public void storeCurrentGame(Game game) {
        dbSharedPref.setCurrentGame(game == null ? null : game.toJson());
    }

    public Game getCurrentGame() {
        String jsonGame = dbSharedPref.getCurrentGame();
        Game game = Game.fromJson(jsonGame);

        return game;
    }

    //----------------------------------------------------------------------------------------

}
