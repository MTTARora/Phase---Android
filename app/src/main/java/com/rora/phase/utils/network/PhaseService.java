package com.rora.phase.utils.network;

import com.rora.phase.model.Banner;
import com.rora.phase.model.Game;
import com.rora.phase.model.Host;
import com.rora.phase.model.Tag;
import com.rora.phase.model.User;
import com.rora.phase.model.api.FilterQuery;
import com.rora.phase.model.api.SearchSuggestion;
import com.rora.phase.model.ui.FilterParams;

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
import retrofit2.http.QueryMap;

public interface PhaseService {

    //------------------- GAME -----------------------

    @GET("./new")
    Call<BaseResponse<List<Game>>> getNewGames(@Query("page") int page, @Query("page_size") int pageSize);

    @GET("./editor_choice")
    //@GET("./")
    Call<BaseResponse<List<Game>>> getEditorsChoice(@Query("page") int page, @Query("page_size") int pageSize);

    @GET("./hot")
    Call<BaseResponse<List<Game>>> getHotGames(@Query("page") int page, @Query("page_size") int pageSize);

    @GET("./trending")
    Call<BaseResponse<List<Game>>> getTrending(@Query("page") int page, @Query("page_size") int pageSize);

    @GET("{game}")
    Call<BaseResponse<Game>> getGame(@Path("game") String gameId);

    @GET("tags")
    Call<BaseResponse<List<Tag>>> getCategoryList();

    @GET("./")
    Call<BaseResponse<List<Game>>> getGameByCategoryList(@Query("tags") String tagName, @Query("page") int page, @Query("page_size") int pageSize);

    @GET("./")
    Call<BaseResponse<List<Game>>> getGameByPayType(@Query("pay_type") String payType, @Query("page") int page, @Query("page_size") int pageSize);

    @GET("./")
    Call<BaseResponse<List<Game>>> getSimilarGameList(@Query("gameId") String gameId, @Query("page") int page, @Query("page_size") int pageSize);

    @GET("./search")
    Call<BaseResponse<List<Game>>> search(@QueryMap FilterQuery filters);

    @GET("./suggest-search")
    Call<BaseResponse<List<SearchSuggestion>>> suggestSearch(@Query("name") String keySearch);


    //--------------------------------------------------


    @GET("banner")
    Call<BaseResponse<List<Banner>>> getBannerList();
}
