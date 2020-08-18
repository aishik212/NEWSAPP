package com.kirtuaishik.newsapp.dagger;

import androidx.lifecycle.LiveData;

import com.kirtuaishik.newsapp.dbase.ArticleTableRepo;
import com.kirtuaishik.newsapp.dbase.ArticlesDAO;
import com.kirtuaishik.newsapp.models.Article_Table;

import java.util.List;

import javax.inject.Inject;

public class ArticleDataSource implements ArticleTableRepo {

    private ArticlesDAO articlesDAO;

    @Inject
    public ArticleDataSource(ArticlesDAO articlesDAO) {
        this.articlesDAO = articlesDAO;
    }

    @Override
    public LiveData<List<Article_Table>> getAllArticles() {
        return articlesDAO.getArticlesSorted();
    }

    @Override
    public void insert(Article_Table articleTable) {
        articlesDAO.insert(articleTable);
    }

    @Override
    public void deleteAll() {
        articlesDAO.deleteAll();
    }
}
