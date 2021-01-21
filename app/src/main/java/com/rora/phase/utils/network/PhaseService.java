package com.rora.phase.utils.network;

import com.rora.phase.model.Game;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PhaseService {
    @GET("users/{user}/repos")
    Call<List<Game>> getNewGames();

    @GET("users/{user}/repos")
    Call<List<Game>> getGame(@Path("id") String gameId);
}
