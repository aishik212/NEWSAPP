package com.kirtuaishik.newsapp.api;

import com.kirtuaishik.newsapp.models.ResponseModel;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiInterface {

    @GET("everything")
    Flowable<ResponseModel> getLatestNews2(@Query("country") String country, @Query("apiKey") String apiKey);
    //--------------------------The RxJava Call Section on Retrofit
}
