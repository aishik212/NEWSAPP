package com.kirtuaishik.newsapp.API;

import com.kirtuaishik.newsapp.models.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiInterface {
    @GET("top-headlines")
    Call<ResponseModel> getLatestNews(@Query("country") String country, @Query("apiKey") String apiKey);
}
