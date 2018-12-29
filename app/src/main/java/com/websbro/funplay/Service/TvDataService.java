package com.websbro.funplay.Service;

import com.websbro.funplay.model.DiscoverGenresResponse;
import com.websbro.funplay.model.PopularTvResponse;
import com.websbro.funplay.model.SearchResponse;
import com.websbro.funplay.model.SimilarShows;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TvDataService {

    @GET("/3/search/tv")
    Call<SearchResponse> getSearchResult(@Query("api_key")String apiKey,@Query("query")String query);

    @GET("/3/discover/tv")
    Call<DiscoverGenresResponse> getGenreShows(@Query("api_key")String apiKey, @Query("with_genres")String genresId);

    @GET("/3/tv/popular")
    Call<PopularTvResponse> getPopularShows(@Query("api_key")String apiKey);

    @GET("/3/tv/{tv_id}/similar")
    Call<SimilarShows> getSililar(@Path("tv_id")String tvId,@Query("api_key") String apiKey);


}