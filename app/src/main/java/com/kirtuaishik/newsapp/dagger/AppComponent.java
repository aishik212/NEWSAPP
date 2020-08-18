package com.kirtuaishik.newsapp.dagger;

import android.app.Application;

import com.kirtuaishik.newsapp.MainActivity;
import com.kirtuaishik.newsapp.dbase.ArticleDatabase;
import com.kirtuaishik.newsapp.dbase.ArticleTableRepo;
import com.kirtuaishik.newsapp.dbase.ArticlesDAO;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class, AppModule.class, RoomModule.class})
public interface AppComponent {

    void inject(MainActivity mainActivity);

    ArticlesDAO productDao();

    ArticleDatabase demoDatabase();

    ArticleTableRepo productRepository();

    Application application();

}