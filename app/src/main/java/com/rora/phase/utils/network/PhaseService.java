package com.rora.phase.utils.network;

import com.rora.phase.model.Game;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PhaseService {
    @GET("./")
    Call<BaseResponse<List<Game>>> getNewGames();

    //@GET("/recent_play")
    @GET("./")
    Call<BaseResponse<List<Game>>> getRecentPlay();

    //@GET("/editor-choice")
    @GET("./")
    Call<BaseResponse<List<Game>>> getEditorsChoice();

    //@GET("/hot")
    @GET("./")
    Call<BaseResponse<List<Game>>> getHotGames();

    @GET("/games/{id}")
    Call<BaseResponse> getGame(String gameId);
}
