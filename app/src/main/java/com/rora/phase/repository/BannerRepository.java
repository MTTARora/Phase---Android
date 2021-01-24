package com.rora.phase.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Banner;
import com.rora.phase.model.Game;
import com.rora.phase.utils.network.BaseResponse;
import com.rora.phase.utils.network.PhaseService;
import com.rora.phase.utils.network.PhaseServiceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerRepository {

    private PhaseService phaseService;
    private MutableLiveData<List<Banner>> bannerList;

    public BannerRepository() {

        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper();
        phaseService = phaseServiceHelper.getPhaseService();

        this.bannerList = new MutableLiveData<>();

    }

    public MutableLiveData<List<Banner>> getBannerList() {
        return bannerList;
    }

    public void getBannerListData() {
        phaseService.getBannerList().enqueue(new Callback<BaseResponse<List<Banner>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Banner>>> call, Response<BaseResponse<List<Banner>>> response) {
                List<Banner> listGame = BaseResponse.getResult(response.body());
                listGame = listGame == null ? new ArrayList<>() : listGame;
                bannerList.postValue(listGame);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Banner>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                bannerList.postValue(new ArrayList<>());
            }
        });
    }

}
