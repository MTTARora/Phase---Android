package com.rora.phase.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.model.User;
import com.rora.phase.utils.DataResultHelper;
import com.rora.phase.utils.network.BaseResponse;
import com.rora.phase.utils.network.PhaseServiceHelper;
import com.rora.phase.utils.network.UserPhaseService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private UserPhaseService userServices;

    private MutableLiveData<User> user;
    private MutableLiveData<List<Game>> favoriteList, recentPlayList, recommendedList;
    private MutableLiveData<DataResultHelper> updatingDataResult;

    public UserRepository(Context context) {
        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper(context);
        userServices = phaseServiceHelper.getUserPhaseService();

        user = new MutableLiveData<>();
        favoriteList =  new MutableLiveData<>();
        recentPlayList =  new MutableLiveData<>();
        recommendedList = new MutableLiveData<>();
        updatingDataResult =  new MutableLiveData<>();
    }

    //--------------------------------GET/SET------------------------

    public MutableLiveData<User> getUser() {
        return user;
    }

    public MutableLiveData<DataResultHelper> getUpdatingDataResult() {
        return updatingDataResult;
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


    public void signUp(String email, String password) {
        userServices.signUp(email, password).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                updatingDataResult.postValue(new DataResultHelper());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                updatingDataResult.postValue(new DataResultHelper("Please try again later!", null));
            }
        });
    }

    public void signIn(String email, String password) {
        userServices.signIn(email, password).enqueue(new Callback<BaseResponse<User>>() {
            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                user.postValue(BaseResponse.getResult(response.body()));
            }

            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                user.postValue(null);
                updatingDataResult.postValue(new DataResultHelper("Please try again later!", null));
            }
        });
    }

    public void forgotPassword(String email) {
        userServices.forgotPassword(email).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                updatingDataResult.postValue(new DataResultHelper());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                updatingDataResult.postValue(new DataResultHelper("Please try again later!", null));
            }
        });
    }

    public void getUserInfo() {
        userServices.getUserInfo().enqueue(new Callback<BaseResponse<User>>() {
            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                user.postValue(BaseResponse.getResult(response.body()));
            }

            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                user.postValue(null);
                updatingDataResult.postValue(new DataResultHelper("Please try again later!", null));
            }
        });
    }

    public void updateUser(User user) {
        userServices.updateInfo(user).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                updatingDataResult.postValue(new DataResultHelper());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                updatingDataResult.postValue(new DataResultHelper("Please try again later!", null));
            }
        });
    }

    public void getFavoriteListData() {

    }

    public void addFavorite(String gameId) {
        userServices.addFavorite(gameId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                updatingDataResult.postValue(new DataResultHelper());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                updatingDataResult.postValue(new DataResultHelper("Please try again later!", null));
            }
        });
    }

    public void removeFavorite(String gameId) {
        userServices.removeFavorite(gameId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                updatingDataResult.postValue(new DataResultHelper());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                updatingDataResult.postValue(new DataResultHelper("Please try again later!", null));
            }
        });
    }


    public void getRecommendedGameListData() {
        userServices.getRecommended().enqueue(new Callback<BaseResponse<List<Game>>>() {
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

}
