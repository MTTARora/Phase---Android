package com.rora.phase.utils.network;

import android.content.Context;

import com.rora.phase.utils.DataResultHelper;
import com.rora.phase.utils.SharedPreferenceHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhaseServiceHelper {

    private PhaseService phaseService;
    private Context context;
    private SharedPreferenceHelper sharedPreferenceHelper;

    private final String basePhaseUrl = "http://roragame.ga/api/";
    private final String userBaseUrl = basePhaseUrl + "users/";
    private final String gameBaseUrl = basePhaseUrl + "games/";

    public PhaseServiceHelper() {
    }

    public PhaseServiceHelper(Context context) {
        this.context = context;
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
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

        phaseService = new retrofit2.Retrofit.Builder()
                .baseUrl(gameBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PhaseService.class);

        return phaseService;

    }


    /** Return service with game api url */

    public PhaseService getUserPhaseService() {

        //Add this for debugging
        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.level(HttpLoggingInterceptor.Level.BODY);
        //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request newRequest  = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + sharedPreferenceHelper.getUserToken())
                    .build();
            return chain.proceed(newRequest);
        }).build();

        phaseService = new retrofit2.Retrofit.Builder()
                .client(client)
                .baseUrl(userBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PhaseService.class);

        return phaseService;

    }

    public static <T> DataResultHelper<T> handleResponse(Response<T> response) {
        DataResultHelper<T> data = new DataResultHelper<>();
        if(response.code() == 200) {
            data.setData(response.body());
        } else {
            data.setErrMsg("Unable to connect to server, please try again later!");
        }
        return data;
    }

}
