package com.rora.phase.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Banner;
import com.rora.phase.model.Game;
import com.rora.phase.utils.network.APIServicesHelper;
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
        APIServicesHelper<List<Banner>> apiHelper = new APIServicesHelper<>();

        apiHelper.request(phaseService.getBannerList(), (err, data) -> {
            if (err != null) {
                bannerList.postValue(new ArrayList<>());
            } else {
                List<Banner> listGame = data;
                listGame = listGame == null ? new ArrayList<>() : listGame;
                bannerList.postValue(listGame);
            }
        });
    }

}
