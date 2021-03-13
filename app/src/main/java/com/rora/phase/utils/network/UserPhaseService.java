package com.rora.phase.utils.network;

import com.rora.phase.model.Game;
import com.rora.phase.model.Host;
import com.rora.phase.model.User;
import com.rora.phase.model.api.LoginCredential;
import com.rora.phase.model.api.LoginResponse;
import com.rora.phase.model.api.PinConfirmBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserPhaseService {

    @POST("./auth/sign_up")
    Call<BaseResponse> signUp(@Body String email, @Body String password);

    @POST("./login")
    Call<BaseResponse<LoginResponse>> signIn(@Body LoginCredential credentials);

    @POST("/forgot_password")
    Call<BaseResponse> forgotPassword(@Body String email);

    @GET("./")
    Call<BaseResponse<User>> getUserInfo();

    @POST("./")
    Call<BaseResponse> updateInfo(@Body User user);

    @GET("/favorite")
    Call<BaseResponse<List<Game>>> getFavorite();

    @FormUrlEncoded
    @POST("/favorite")
    Call<BaseResponse> addFavorite(@Field("game_id") String gameId);

    @FormUrlEncoded
    @DELETE("/favorite")
    Call<BaseResponse> removeFavorite(@Field("game_id") String gameId);

    @GET("/recent_play")
    Call<BaseResponse<List<Game>>> getRecentPlay(@Query("page") int page, @Query("page_size") int pageSize);

    @GET("/recommended")
    Call<BaseResponse<List<Game>>> getRecommended(@Query("page") int page,@Query("page_size") int pageSize);

    @GET("./play/available-host")
    Call<BaseResponse<Host>> getComputerIP();

    @POST("./play/confirm-pin")
    Call<BaseResponse> sendPinToHost(@Body PinConfirmBody confirmData);

}
