package com.kirtuaishik.newsapp;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ArticlesRepository {
    private ArticlesDAO articlesDAO;
    private LiveData<List<Articles>> allArticles;

    ArticlesRepository(Application application) {
        ArticleDatabase articleDatabase = ArticleDatabase.getDatabase(application);
        articlesDAO = articleDatabase.articlesDAO();
        allArticles = articlesDAO.getArticlesSorted();
    }

    LiveData<List<Articles>> getAllArticles() {
        return allArticles;
    }

    void insert(Articles articles) {
        ArticleDatabase.databaseWriteExecutor.execute(() ->
        {
            articlesDAO.insert(articles);
        });
    }

    void deleteAll() {
        ArticleDatabase.databaseWriteExecutor.execute(() -> {
            articlesDAO.deleteAll();
        });
    }
}
