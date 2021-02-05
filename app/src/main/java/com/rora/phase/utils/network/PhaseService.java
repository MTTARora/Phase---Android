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
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PhaseService {

    //------------------- GAME -----------------------

    @GET("./")
    Call<BaseResponse<List<Game>>> getNewGames();

    //@GET("/editor-choices")
    @GET("./")
    Call<BaseResponse<List<Game>>> getEditorsChoice();

    //@GET("/hot-games")
    @GET("./")
    Call<BaseResponse<List<Game>>> getHotGames();

    //@GET("/hot-games")
    @GET("./")
    Call<BaseResponse<List<Game>>> getTrending();

    @GET("/games/{id}")
    Call<BaseResponse> getGame(String gameId);

    @GET("tags")
    Call<BaseResponse<List<Tag>>> getCategoryList();

    @GET("./")
    Call<BaseResponse<List<Game>>> getGameByCategoryList(@Query("tagName") String tagName);

    @GET("./")
    Call<BaseResponse<List<Game>>> getGameByPayType();

    @GET("./")
    Call<BaseResponse<String>> getComputerIP();

    //--------------------------------------------------


    @GET("banner")
    Call<BaseResponse<List<Banner>>> getBannerList();
}
