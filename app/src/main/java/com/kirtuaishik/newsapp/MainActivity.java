package com.kirtuaishik.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.kirtuaishik.newsapp.adapters.ArticleListAdapter;
import com.kirtuaishik.newsapp.api.ApiClient;
import com.kirtuaishik.newsapp.api.NewsApiInterface;
import com.kirtuaishik.newsapp.dagger.AppModule;
import com.kirtuaishik.newsapp.dagger.DaggerAppComponent;
import com.kirtuaishik.newsapp.dagger.RoomModule;
import com.kirtuaishik.newsapp.dbase.ArticleTableRepo;
import com.kirtuaishik.newsapp.models.Article_Table;
import com.kirtuaishik.newsapp.models.OnlineArticleModel;
import com.kirtuaishik.newsapp.models.ResponseModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    Button US, IN;
    public static boolean showAD = true;

    @Inject
    String getApiKey;
    //----------------------------using dagger to get api key------------------------------------
    /*
    @Inject
    Drawable drawable;
    */
    public static final String TAG = "news_app_log";

    @Inject
    public ArticleTableRepo articleTableRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: key " + TAG + " ");


        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        articleTableRepo.getAllArticles().observe(this, new Observer<List<Article_Table>>() {
            @Override
            public void onChanged(List<Article_Table> article_tables) {
                for (Article_Table a : article_tables) {
                    Log.d(TAG, "onChanged: " + a.getId() + " " + a.getTitle());
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.rv);
        US = findViewById(R.id.usa);
        IN = findViewById(R.id.india);
        final ArticleListAdapter adapter = new ArticleListAdapter(this, null);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        final NewsApiInterface apiInterface = ApiClient.getClient().create(NewsApiInterface.class);
        SharedPreferences preferences = getSharedPreferences("data", 0);
        String string = preferences.getString("country", "in");
        final String[] Country = {string};
        String locale = getResources().getConfiguration().locale.getCountry();
        //-------------------The Location Section-----------------------
        if (locale.equals("IN") || locale.equals("US")) {
            Country[0] = locale;
        }
        Country[0] = "IN";
        articleTableRepo.getAllArticles().observe(this, new Observer<List<Article_Table>>() {
            @Override
            public void onChanged(List<Article_Table> articles) {
                mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                        }
                        showAD = mFirebaseRemoteConfig.getBoolean("showAD");
                        adapter.setArticles(articles, MainActivity.this);
                    }
                });
            }
        });
        callAPI(apiInterface, Country[0]);
        US.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Country[0] = "US";
                preferences.edit().putString("country", Country[0]).apply();
                callAPI(apiInterface, Country[0]);
            }
        });

        IN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                }
                Log.d(TAG, "onComplete: " + mFirebaseRemoteConfig.getBoolean("showAD"));
                showAD = mFirebaseRemoteConfig.getBoolean("showAD");
                callAPI(apiInterface, Country[0]);
            }
        });
    }

    private void callAPI(NewsApiInterface apiInterface, String country) {
        //RXJAVA
        Log.d(TAG, "callAPI: " + getApiKey);
        if (getApiKey != null) {
            apiInterface.getLatestNews2(country, getApiKey)
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(new io.reactivex.Observer<ResponseModel>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d(TAG, "onSubscribe: ");
                        }

                        @Override
                        public void onNext(ResponseModel responseModel) {
                            articleTableRepo.deleteAll();
                            updateArticles(responseModel.getOnlineArticleModels());
                            Log.d(TAG, "onNext: " + responseModel.getTotalResults());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError: " + e.getLocalizedMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete: ");
                        }
                    });
        }
    }

    private void updateArticles(List<OnlineArticleModel> a) {
        for (OnlineArticleModel b : a) {
            Article_Table word = new Article_Table(b.getTitle().hashCode(), b.getTitle(), b.getPublishedAt(), b.getUrlToImage(), b.getUrl());
            articleTableRepo.insert(word);
        }
    }

}