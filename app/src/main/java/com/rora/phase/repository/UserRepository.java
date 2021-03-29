package com.rora.phase.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.rora.phase.model.Game;
import com.rora.phase.model.User;
import com.rora.phase.model.UserPlayingData;
import com.rora.phase.model.api.FindingHostResponse;
import com.rora.phase.model.api.LoginCredential;
import com.rora.phase.model.api.LoginResponse;
import com.rora.phase.model.api.PinConfirmBody;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.utils.DataResponse;
import com.rora.phase.utils.SharedPreferencesHelper;
import com.rora.phase.utils.callback.OnResultCallBack;
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
        userServices.signIn(loginCredential).enqueue(new Callback<BaseResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
                DataResponse<BaseResponse<LoginResponse>> dataResponse = PhaseServiceHelper.handleResponse(response);

                if (dataResponse.getMsg() != null) {
                    updateDataResult.postValue(new DataResponse(dataResponse.getMsg(), null));
                } else {
                    LoginResponse resp = BaseResponse.getResult(dataResponse.getData());
                    User user = resp.getInfo();
                    String token = resp.getToken();

                    if (token != null && user != null) {
                        storeLocalUser(user.getUserName(), token);
                    }
                    updateDataResult.postValue(new DataResponse(null, null));
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                updateDataResult.postValue(new DataResponse("Please try again later!", null));
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

    public void getUserInfo() {
        userAuthenticatedServices.getUserInfo().enqueue(new Callback<BaseResponse<User>>() {
            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                user.postValue(BaseResponse.getResult(response.body()));
            }

            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                user.postValue(null);
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

    public void getRecommendedGameListData(int page, int pageSize) {
        userAuthenticatedServices.getRecommended(page, pageSize).enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                List<Game> list = BaseResponse.getResult(response.body());
                list = list == null ? new ArrayList<>() : list;
                recommendedList.postValue(list);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                recommendedList.postValue(new ArrayList<>());
            }
        });
    }

    public void getRecentPlayData() {

    }

    public void getComputerData(OnResultCallBack<ComputerDetails> callBack) {
        userAuthenticatedServices.getComputerIP().enqueue(new Callback<BaseResponse<FindingHostResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<FindingHostResponse>> call, Response<BaseResponse<FindingHostResponse>> response) {
                DataResponse<BaseResponse<FindingHostResponse>> dataResponse = PhaseServiceHelper.handleResponse(response);
                String err = dataResponse.getMsg();
                if (err != null) {
                    callBack.onResult(err, null);
                } else {
                    FindingHostResponse resp = BaseResponse.getResult(dataResponse.getData());

                    if (resp.queue != null || resp.host == null)
                        callBack.onResult("So many players are playing right now, please try again later!", null);
                    else
                        callBack.onResult(null, new ComputerDetails(resp.host));
                    //computer.postValue(new ComputerDetails(host));
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<FindingHostResponse>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                callBack.onResult("Can't connect to server, please try again later!", null);
                //computer.postValue(null);
            }
        });
    }

    public void sendPinToHost(String pinStr, String hostId, OnResultCallBack<ComputerDetails> callBack) {
        PinConfirmBody body = new PinConfirmBody(pinStr, hostId);
        userAuthenticatedServices.sendPinToHost(body).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                DataResponse<BaseResponse> dataResponse = PhaseServiceHelper.handleResponse(response);
                String err = dataResponse.getMsg();
                if (err != null)
                    callBack.onResult(err, null);
                else
                    callBack.onResult(null, null);
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                callBack.onResult("Can't connect to server, please try again later!", null);
            }
        });
    }

    public void stopPlaying() {

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

    //----------------------------------------------------------------------------------------

}
