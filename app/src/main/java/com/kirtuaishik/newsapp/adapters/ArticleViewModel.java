package com.kirtuaishik.newsapp.adapters;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kirtuaishik.newsapp.DBase.ArticlesRepository;
import com.kirtuaishik.newsapp.models.Articles;

import java.util.List;


public class ArticleViewModel extends AndroidViewModel {
    private ArticlesRepository mRepository;
    private LiveData<List<Articles>> mAllWords;

    public ArticleViewModel(Application application) {
        super(application);
        mRepository = new ArticlesRepository(application);
        mAllWords = mRepository.getAllArticles();
    }

    public LiveData<List<Articles>> getAllWords() {
        return mAllWords;
    }

    public void insert(Articles articles) {
        mRepository.insert(articles);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }
}
