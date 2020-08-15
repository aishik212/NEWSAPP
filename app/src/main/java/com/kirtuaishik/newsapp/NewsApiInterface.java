package com.kirtuaishik.newsapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiInterface {
    @GET("top-headlines")
    Call<ResponseModel> getLatestNews(@Query("country") String country, @Query("apiKey") String apiKey);
}
