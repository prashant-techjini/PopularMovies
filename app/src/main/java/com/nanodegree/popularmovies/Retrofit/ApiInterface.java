package com.nanodegree.popularmovies.Retrofit;

import com.nanodegree.popularmovies.Model.GetMoviesResponseModel;
import com.nanodegree.popularmovies.Model.GetReviewsResponseModel;
import com.nanodegree.popularmovies.Model.GetTrailorsResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Prashant Nayak
 */
public interface ApiInterface {

    @GET("movie/top_rated")
    Call<GetMoviesResponseModel> movie_list_sort_toprated(
            @Query("api_key") String api_key
    );

    @GET("movie/popular")
    Call<GetMoviesResponseModel> movie_list_sort_popular(
            @Query("api_key") String api_key
    );

    @GET("movie/{id}/videos")
    Call<GetTrailorsResponseModel> trailor_list(
            @Path("id") long id,
            @Query("api_key") String api_key
    );

    @GET("movie/{id}/reviews")
    Call<GetReviewsResponseModel> review_list(
            @Path("id") long id,
            @Query("api_key") String api_key
    );

}
