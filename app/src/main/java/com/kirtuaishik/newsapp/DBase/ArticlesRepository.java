package com.kirtuaishik.newsapp.DBase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.kirtuaishik.newsapp.models.Articles;

import java.util.List;

public class ArticlesRepository {
    private ArticlesDAO articlesDAO;
    private LiveData<List<Articles>> allArticles;

    public ArticlesRepository(Application application) {
        ArticleDatabase articleDatabase = ArticleDatabase.getDatabase(application);
        articlesDAO = articleDatabase.articlesDAO();
        allArticles = articlesDAO.getArticlesSorted();
    }

    public LiveData<List<Articles>> getAllArticles() {
        return allArticles;
    }

    public void insert(Articles articles) {
        ArticleDatabase.databaseWriteExecutor.execute(() ->
        {
            articlesDAO.insert(articles);
        });
    }

    public void deleteAll() {
        ArticleDatabase.databaseWriteExecutor.execute(() -> {
            articlesDAO.deleteAll();
        });
    }
}
