package com.rora.phase.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.model.User;
import com.rora.phase.utils.DataResultHelper;
import com.rora.phase.utils.network.BaseResponse;
import com.rora.phase.utils.network.PhaseService;
import com.rora.phase.utils.network.PhaseServiceHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private PhaseService phaseService;

    private MutableLiveData<User> user;
    private MutableLiveData<List<Game>> favoriteList, recentPlayList;
    private MutableLiveData<DataResultHelper> updatingDataResult;

    public UserRepository(Context context) {
        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper(context);
        phaseService = phaseServiceHelper.getUserPhaseService();

        user = new MutableLiveData<>();
        favoriteList =  new MutableLiveData<>();
        recentPlayList =  new MutableLiveData<>();
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

    //---------------------------------------------------------------


    public void signUp(String email, String password) {
        phaseService.signUp(email, password).enqueue(new Callback<BaseResponse>() {
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
        phaseService.signIn(email, password).enqueue(new Callback<BaseResponse<User>>() {
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
        phaseService.forgotPassword(email).enqueue(new Callback<BaseResponse>() {
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
        phaseService.getUserInfo().enqueue(new Callback<BaseResponse<User>>() {
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
        phaseService.updateInfo(user).enqueue(new Callback<BaseResponse>() {
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
        phaseService.addFavorite(gameId).enqueue(new Callback<BaseResponse>() {
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
        phaseService.removeFavorite(gameId).enqueue(new Callback<BaseResponse>() {
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

    public void getRecentPlayData() {

    }

}
