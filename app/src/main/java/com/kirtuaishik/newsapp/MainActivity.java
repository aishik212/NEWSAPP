package com.kirtuaishik.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ArticleViewModel articleViewModel;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public static final String API_KEY = "7876eea480474b59b25e82866d2f6374";
    public static String TAG = "news_app_log";
    Button US, IN;
    private static RecyclerView recyclerView;
    Call<ResponseModel> articleCall;
    public static boolean showAD = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv);
        US = findViewById(R.id.usa);
        IN = findViewById(R.id.india);
        final ArticleListAdapter adapter = new ArticleListAdapter(this);
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
                            Toast.makeText(MainActivity.this, "Fetch and activate succeeded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                        Log.d(TAG, "onComplete: " + mFirebaseRemoteConfig.getBoolean("showAD"));
                        showAD = mFirebaseRemoteConfig.getBoolean("showAD");
                        adapter.setArticles(articles, MainActivity.this);
                    }
                });
            }
        });
        final NewsApiInterface apiInterface = ApiClient.getClient().create(NewsApiInterface.class);
        articleCall = apiInterface.getLatestNews("us", API_KEY);
        US.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articleViewModel.deleteAll();
                articleCall = apiInterface.getLatestNews("us", API_KEY);
                loadDB();
            }
        });

        IN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articleViewModel.deleteAll();
                articleCall = apiInterface.getLatestNews("in", API_KEY);
                loadDB();
            }
        });
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    boolean updated = task.getResult();
                    Toast.makeText(MainActivity.this, "Fetch and activate succeeded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onComplete: " + mFirebaseRemoteConfig.getBoolean("showAD"));
                showAD = mFirebaseRemoteConfig.getBoolean("showAD");
                loadDB();
            }
        });
    }

    private void loadDB() {
        articleCall.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NotNull Call<ResponseModel> call, @NotNull Response<ResponseModel> response) {
                if (response.body() != null && response.body().getStatus().equals("ok")) {
                    List<Article> a = response.body().getArticles();
                    if (a.size() > 0) {
                        for (Article b : a) {
                            Articles word = new Articles(b.getTitle().hashCode(), b.getTitle(), b.getPublishedAt(), b.getUrlToImage(), b.getUrl());
                            articleViewModel.insert(word);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
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