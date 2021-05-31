package com.rora.phase.utils.network;

import android.content.Context;

import com.rora.phase.RoraLog;
import com.rora.phase.utils.DataResponse;
import com.rora.phase.utils.SharedPreferencesHelper;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhaseServiceHelper {

    private PhaseService phaseService;
    private UserPhaseService userPhaseService;
    private Context context;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private static final String basePhaseHttpsUrl = "https://roragame.rorasoft.com/api/";
    private static final String basePhaseHttpUrl = "http://roragame.rorasoft.com/api/";
    //private static final String basePhaseHttpsUrl = "http://10.0.2.2:53315/api/";
    //private static final String basePhaseHttpUrl = "http://10.0.2.2:53315/api/";
    private final String userBaseUrl = basePhaseHttpsUrl + "users/";
    private final String userAuthBaseUrl = basePhaseHttpsUrl + "auth/";
    private final String gameBaseUrl = basePhaseHttpUrl + "games/";

    public static final String playHubUrl = basePhaseHttpsUrl + "playing-game-hub";

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
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (!sharedPreferencesHelper.getUserToken().isEmpty()) {
            httpClient.addInterceptor(chain -> {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + sharedPreferencesHelper.getUserToken())
                        .build();
                return chain.proceed(newRequest);
            });
        }

        httpClient.addInterceptor(logging);

        phaseService = new retrofit2.Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(gameBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PhaseService.class);

        return phaseService;
    }

    /** Return service with game api url */

    public UserPhaseService getUserPhaseService(boolean auth) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (auth) {
            httpClient.addInterceptor(chain -> {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + sharedPreferencesHelper.getUserToken())
                        .build();
                return chain.proceed(newRequest);
            });
        }

        httpClient.addInterceptor(logging);

        userPhaseService = new retrofit2.Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(auth ? userBaseUrl : userAuthBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserPhaseService.class);

        return userPhaseService;
    }

    public static <T> DataResponse<T> handleResponse(Response<T> response) {
        DataResponse<T> data = new DataResponse<>();
        if(response.code() == 200) {
            data.setData(response.body());
        } else {
            String err = "Could not get data from server, please try again later!";
            try {
                if (response.code() == 401) {
                    RoraLog.warning("Trying to detect error from server failed: " + response.code() + " - " + response.headers().get("www-authenticate"));
                    err = String.valueOf(401);
                } else if (response.errorBody() != null || response.body() != null) {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    err = jObjError.getString("message");
                }
            } catch (Exception e) {
                RoraLog.warning("Trying to detect error from server failed: " + response.code() + " - " + e.getMessage());
            }

            data.setMsg(err);
        }
        return data;
    }
}
