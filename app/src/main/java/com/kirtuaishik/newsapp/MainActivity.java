package com.kirtuaishik.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.kirtuaishik.newsapp.API.ApiClient;
import com.kirtuaishik.newsapp.API.NewsApiInterface;
import com.kirtuaishik.newsapp.adapters.ArticleListAdapter;
import com.kirtuaishik.newsapp.adapters.ArticleViewModel;
import com.kirtuaishik.newsapp.models.Article;
import com.kirtuaishik.newsapp.models.Articles;
import com.kirtuaishik.newsapp.models.ResponseModel;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends DaggerAppCompatActivity {
    private ArticleViewModel articleViewModel;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    Button US, IN;
    public static boolean showAD = true;

    @Inject
    String getApiKey;
    //----------------------------using dagger to get api key------------------------------------
    public static String TAG = "news_app_log";

    @Inject
    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: key " + getApiKey + " " + drawable);

        RecyclerView recyclerView = findViewById(R.id.rv);
        US = findViewById(R.id.usa);
        IN = findViewById(R.id.india);
        final ArticleListAdapter adapter = new ArticleListAdapter(this, drawable);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleViewModel = new ViewModelProvider(this).get(ArticleViewModel.class);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        articleViewModel.getAllWords().observe(this, new Observer<List<Articles>>() {
            @Override
            public void onChanged(List<Articles> articles) {
                mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                        } else {
                        }
                        Log.d(TAG, "onComplete: " + mFirebaseRemoteConfig.getBoolean("showAD"));
                        showAD = mFirebaseRemoteConfig.getBoolean("showAD");
                        adapter.setArticles(articles, MainActivity.this);
                    }
                });
            }
        });
        final NewsApiInterface apiInterface = ApiClient.getClient().create(NewsApiInterface.class);
        SharedPreferences preferences = getSharedPreferences("data", 0);
        String string = preferences.getString("country", "in");
        final String[] Country = {string};
        String locale = getResources().getConfiguration().locale.getCountry();
        //-------------------The Location Section-----------------------
        if (locale.equals("IN") || locale.equals("US")) {
            Country[0] = locale;
            Toast.makeText(this, "Showing News From " + locale, Toast.LENGTH_SHORT).show();
        }
        callAPI(apiInterface, Country[0]);
        US.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articleViewModel.deleteAll();
                Country[0] = "US";
                preferences.edit().putString("country", Country[0]).apply();
                callAPI(apiInterface, Country[0]);
            }
        });

        IN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articleViewModel.deleteAll();
                Country[0] = "IN";
                preferences.edit().putString("country", Country[0]).apply();
                callAPI(apiInterface, Country[0]);
            }
        });
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    boolean updated = task.getResult();
                } else {
                }
                Log.d(TAG, "onComplete: " + mFirebaseRemoteConfig.getBoolean("showAD"));
                showAD = mFirebaseRemoteConfig.getBoolean("showAD");
                callAPI(apiInterface, Country[0]);
            }
        });
    }

    private void callAPI(NewsApiInterface apiInterface, String country) {
        //RXJAVA
        if (getApiKey != null) {
            apiInterface.getLatestNews2(country, getApiKey)
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(new io.reactivex.Observer<ResponseModel>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseModel responseModel) {
                            updateArticles(responseModel.getArticles());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    private void updateArticles(List<Article> a) {
        for (Article b : a) {
            Articles word = new Articles(b.getTitle().hashCode(), b.getTitle(), b.getPublishedAt(), b.getUrlToImage(), b.getUrl());
            articleViewModel.insert(word);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}