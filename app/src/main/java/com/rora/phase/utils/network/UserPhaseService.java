package com.rora.phase.utils.network;

import com.rora.phase.model.Banner;
import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.model.User;

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

    @POST("/sign_up")
    Call<BaseResponse> signUp(@Body String email, @Body String password);

    @POST("/sign_in")
    Call<BaseResponse<User>> signIn(@Body String email, @Body String password);

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
    Call<BaseResponse<List<Game>>> getRecommended();

}
