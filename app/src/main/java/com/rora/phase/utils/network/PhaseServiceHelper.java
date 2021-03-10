package com.rora.phase.utils.network;

import android.content.Context;

import com.rora.phase.utils.DataResultHelper;
import com.rora.phase.utils.SharedPreferencesHelper;

import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhaseServiceHelper {

    private PhaseService phaseService;
    private UserPhaseService userPhaseService;
    private Context context;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private final String basePhaseHttpsUrl = "https://roragame.ga/api/";
    private final String basePhaseHttpUrl = "http://roragame.ga/api/";
    //private final String basePhaseHttpsUrl = "http://localhost:53315/api/";
    //private final String basePhaseHttpUrl = "http://localhost:53315/api/";
    private final String userBaseUrl = basePhaseHttpUrl + "users/";
    private final String userAuthBaseUrl = basePhaseHttpsUrl + "auth/";
    private final String gameBaseUrl = basePhaseHttpUrl + "games/";

    public PhaseServiceHelper() {
    }

    public PhaseServiceHelper(Context context) {
        this.context = context;
        sharedPreferencesHelper = new SharedPreferencesHelper(context);
    }


    /** Return service with root api url */

    public PhaseService getPhaseService() {

        phaseService = new retrofit2.Retrofit.Builder()
                .baseUrl(basePhaseHttpUrl)
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

    public UserPhaseService getUserPhaseService() {

        //Add this for debugging
        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.level(HttpLoggingInterceptor.Level.BODY);
        //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
        //    Request newRequest  = chain.request().newBuilder()
        //            .addHeader("Authorization", "Bearer " + sharedPreferenceHelper.getUserToken())
        //            .build();
        //    return chain.proceed(newRequest);
        //}).build();

        userPhaseService = new retrofit2.Retrofit.Builder()
                //.client(client)
                .baseUrl(userBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserPhaseService.class);

        return userPhaseService;

    }

    public UserPhaseService getUserAuthPhaseService() {
        userPhaseService = new retrofit2.Retrofit.Builder()
                //.client(client)
                .baseUrl(userAuthBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserPhaseService.class);

        return userPhaseService;
    }

    public static <T> DataResultHelper<T> handleResponse(Response<T> response) {
        DataResultHelper<T> data = new DataResultHelper<>();
        if(response.code() == 200) {
            data.setData(response.body());
        } else {
            data.setErrMsg("No data to fetch, please try again later!");
        }
        return data;
    }

}
