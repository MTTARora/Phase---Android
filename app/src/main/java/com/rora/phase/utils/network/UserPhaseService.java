package com.rora.phase.utils.network;

import com.rora.phase.model.Game;
import com.rora.phase.model.Transaction;
import com.rora.phase.model.User;
import com.rora.phase.model.Wallet;
import com.rora.phase.model.api.FindingHostResponse;
import com.rora.phase.model.api.LoginCredential;
import com.rora.phase.model.api.LoginResponse;
import com.rora.phase.model.api.PinConfirmData;
import com.rora.phase.model.api.PrepareAppModel;
import com.rora.phase.model.api.SignUpCredential;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserPhaseService {

    @POST("./register")
    Call<BaseResponse> signUp(@Body SignUpCredential credential);

    @POST("./login")
    Call<BaseResponse<LoginResponse>> signIn(@Body LoginCredential credentials);

    @POST("forgot-password")
    Call<BaseResponse> forgotPassword(@Body User email);

    @POST("reset_password")
    Call<BaseResponse> resetPassword(@Body String email);

    @POST("./verify_mail")
    Call<BaseResponse> verifyEmail(@Body String email);

    @GET("./")
    Call<BaseResponse<User>> getUserInfo();

    @POST("./")
    Call<BaseResponse> updateInfo(@Body User user);

    @GET("./wallet-information")
    Call<BaseResponse<Wallet>> getWallet();

    @GET("./transactions")
    Call<BaseResponse<List<Transaction>>> getTransactions();

    @POST("./deposit")
    Call<BaseResponse<String>> deposit(@Body Double amount);

    //-------------- LIBRARY ---------------

    @GET("./favorite")
    Call<BaseResponse<List<Game>>> getFavorites();

    @POST("favorite/{gameId}")
    Call<BaseResponse<String>> addFavorite(@Path("gameId") String gameId);

    @PUT("favorite/{gameId}")
    Call<BaseResponse<String>> removeFavorite(@Path("gameId") String gameId);

    @GET("./recent-play")
    Call<BaseResponse<List<Game>>> getRecentPlay(@Query("page") int page, @Query("page_size") int pageSize);

    @GET("/recommended")
    Call<BaseResponse<List<Game>>> getRecommended(@Query("page") int page,@Query("page_size") int pageSize);

    //---------- PLAY ----------

    @GET("./play/available-host")
    Call<BaseResponse<FindingHostResponse>> getAvailableHost(@Query("deviceId") String deviceId, @Query("deviceName") String deviceName, @Query("gameId") String gameId);

    @POST("./play/confirm-pin")
    Call<BaseResponse> sendPinToHost(@Body PinConfirmData confirmData);

    @POST("./play/prepare-app")
    Call<BaseResponse<String>> prepareApp(@Body PrepareAppModel data);
}
