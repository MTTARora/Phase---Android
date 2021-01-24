package com.rora.phase.utils.network;

import android.os.Bundle;
import android.util.Log;

import com.rora.phase.R;
import com.rora.phase.utils.DataHelper;

import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhaseServiceHelper {

    private final String basePhaseUrl = "http://roragame.ga/api/";
    private final String gameBaseUrl = basePhaseUrl + "games/";
    private PhaseService phaseService;

    public PhaseServiceHelper() {
        
    }


    /** Return service with root api url */

    public PhaseService getPhaseService() {

        phaseService = new retrofit2.Retrofit.Builder()
                .baseUrl(basePhaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PhaseService.class);

        return phaseService;
    }


    /** Return service with game api url */

    public PhaseService getGamePhaseService() {

        //Add this for debugging
        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.level(HttpLoggingInterceptor.Level.BODY);
        //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        phaseService = new retrofit2.Retrofit.Builder()
                .baseUrl(gameBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PhaseService.class);

        return phaseService;
    }

    public static <T> DataHelper<T> handleResponse(Response<T> response) {
        DataHelper<T> data = new DataHelper<>();
        if(response.code() == 200) {
            data.setData(response.body());
        } else {
            data.setErrMsg("Unable to connect to server, please try again later!");
        }
        return data;
    }
}
