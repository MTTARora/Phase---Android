package com.rora.phase.utils.network;

import android.content.Context;

import com.rora.phase.utils.DataResultHelper;
import com.rora.phase.utils.SharedPreferencesHelper;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhaseServiceHelper {

    private PhaseService phaseService;
    private UserPhaseService userPhaseService;
    private Context context;
    private SharedPreferencesHelper sharedPreferencesHelper;

    //private static final String basePhaseHttpsUrl = "https://roragame.ga/api/";
    //private static final String basePhaseHttpUrl = "http://roragame.ga/api/";
    private static final String basePhaseHttpsUrl = "http://10.0.2.2:53315/api/";
    private static final String basePhaseHttpUrl = "http://10.0.2.2:53315/api/";
    private final String userBaseUrl = basePhaseHttpUrl + "users/";
    private final String userAuthBaseUrl = basePhaseHttpsUrl + "auth/";
    private final String gameBaseUrl = basePhaseHttpUrl + "games/";

    public static final String playHubUrl = basePhaseHttpUrl + "playing-game-hub";

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
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        userPhaseService = new retrofit2.Retrofit.Builder()
                .client(httpClient.build())
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
            String err = "No data to fetch, please try again later!";
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                err = jObjError.getString("message");
            } catch (Exception e) {
                err = e.getMessage();
            }
            data.setErrMsg(err);
        }
        return data;
    }

}
