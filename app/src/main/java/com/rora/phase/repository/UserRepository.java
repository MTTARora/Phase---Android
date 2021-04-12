package com.rora.phase.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.rora.phase.RoraLog;
import com.rora.phase.model.Game;
import com.rora.phase.model.User;
import com.rora.phase.model.UserPlayingData;
import com.rora.phase.model.api.FindingHostResponse;
import com.rora.phase.model.api.LoginCredential;
import com.rora.phase.model.api.LoginResponse;
import com.rora.phase.model.api.PinConfirmBody;
import com.rora.phase.model.api.PrepareAppModel;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.utils.DataResponse;
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
    private SharedPreferencesHelper dbSharedPref;

    private MutableLiveData<User> user;
    private MutableLiveData<List<Game>> favoriteList, recentPlayList, recommendedList;
    private MutableLiveData<DataResponse> updateDataResult;

    public static UserRepository newInstance(Context context) {
        return new UserRepository(context);
    }

    public UserRepository(Context context) {
        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper(context);
        userAuthenticatedServices = phaseServiceHelper.getUserPhaseService(true);
        userServices = phaseServiceHelper.getUserPhaseService(false);
        dbSharedPref = new SharedPreferencesHelper(context);

        user = new MutableLiveData<>();
        favoriteList =  new MutableLiveData<>();
        recentPlayList =  new MutableLiveData<>();
        recommendedList = new MutableLiveData<>();
        updateDataResult =  new MutableLiveData<>();
    }

    //--------------------------------GET/SET------------------------

    public MutableLiveData<User> getUser() {
        return user;
    }

    public MutableLiveData<DataResponse> getUpdateDataResult() {
        return updateDataResult;
    }

    public MutableLiveData<List<Game>> getFavoriteList() {
        return favoriteList;
    }

    public MutableLiveData<List<Game>> getRecentPlayList() {
        return recentPlayList;
    }

    public MutableLiveData<List<Game>> getRecommendedGameList() {
        return recommendedList;
    }

    //---------------------------------------------------------------


    //--------------------------------- NETWORK SERVICES -----------------------------------

    public void signUp(String email, String password) {
        userServices.signUp(email, password).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                updateDataResult.postValue(new DataResponse());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                updateDataResult.postValue(new DataResponse("Please try again later!", null));
            }
        });
    }

    public void signIn(LoginCredential loginCredential) {
        APIServicesHelper<LoginResponse> apiHelper = new APIServicesHelper<>();

        apiHelper.request(userServices.signIn(loginCredential), (err, data) -> {
            if (err != null) {
                updateDataResult.postValue(new DataResponse(err, null));
            } else {
                User user = data.getInfo();
                String token = data.getToken();

                if (token != null && user != null) {
                    storeLocalUser(user.getUserName(), token);
                }
                updateDataResult.postValue(new DataResponse(null, data));
            }
        });
    }

    public void signOut() {
        storeLocalUser(null, null);
    }

    public void forgotPassword(String email) {
        userServices.forgotPassword(email).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                updateDataResult.postValue(new DataResponse());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                updateDataResult.postValue(new DataResponse("Please try again later!", null));
            }
        });
    }

    public void updateUser(User user) {
        userAuthenticatedServices.updateInfo(user).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                updateDataResult.postValue(new DataResponse());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                updateDataResult.postValue(new DataResponse("Please try again later!", null));
            }
        });
    }

    public void getFavoriteListData() {

    }

    public void addFavorite(String gameId) {
        userAuthenticatedServices.addFavorite(gameId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                updateDataResult.postValue(new DataResponse());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                updateDataResult.postValue(new DataResponse("Please try again later!", null));
            }
        });
    }

    public void removeFavorite(String gameId) {
        userAuthenticatedServices.removeFavorite(gameId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                updateDataResult.postValue(new DataResponse());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                updateDataResult.postValue(new DataResponse("Please try again later!", null));
            }
        });
    }

    public void getRecentPlayData() {

    }

    public void getComputerData(OnResultCallBack<FindingHostResponse> callBack) {
        APIServicesHelper<FindingHostResponse> apiHelper = new APIServicesHelper<>();

        apiHelper.request(userAuthenticatedServices.getComputerIP(), (err, data) -> {
            if (err != null) {
                callBack.onResult(err, null);
            } else {
                if (data.queue != null || data.host == null) {
                    RoraLog.info("So many players are playing right now, please wait!");
                    callBack.onResult(null, data);
                }
                else
                    callBack.onResult(null, data);
            }
        });
    }

    public void sendPinToHost(String pinStr, String hostId, OnResultCallBack<ComputerDetails> callBack) {
        APIServicesHelper apiHelper = new APIServicesHelper<>();

        apiHelper.request(userAuthenticatedServices.sendPinToHost(new PinConfirmBody(pinStr, hostId)), (err, data) -> {
            if (err != null && !err.contains("success")) {
                callBack.onResult(err, null);
            } else
                callBack.onResult(null, null);
        });
    }

    public void prepareAppHost(String gameId, String platformId, String platformUsername, String platformPassword, OnResultCallBack<String> callBack) {
        APIServicesHelper apiHelper = new APIServicesHelper<>();
        PrepareAppModel data = new PrepareAppModel(gameId, platformId, platformUsername, platformPassword);
        apiHelper.request(userAuthenticatedServices.prepareApp(data), (err, result) -> {
            if (err != null && !err.contains("success")) {
                callBack.onResult(err, null);
            } else
                callBack.onResult(null, null);
        });
    }

    //----------------------------------------------------------------------------------------


    //------------------------------------ LOCAL SERVICES ------------------------------------

    public String getUserToken() {
        return dbSharedPref.getUserToken();
    }

    public String getUserName() {
        return dbSharedPref.getUserName();
    }

    public void storeLocalUser(String userName, String token) {
        dbSharedPref.setUserName(userName);
        dbSharedPref.setUserToken(token);
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
        return dbSharedPref.getUserPlayState().equals(UserPlayingData.PlayingState.STOP.id);
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
