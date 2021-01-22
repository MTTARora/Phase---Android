package com.rora.phase.utils.network;

import android.os.Bundle;
import android.util.Log;

import retrofit2.converter.gson.GsonConverterFactory;

public class PhaseServiceHelper {

    private final String basePhaseUrl = "http://roragame-test.ap-southeast-1.elasticbeanstalk.com/api/";
    private final String gameBaseUrl = basePhaseUrl + "games/";
    private PhaseService phaseService;

    public PhaseServiceHelper() {
        
    }

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
}
