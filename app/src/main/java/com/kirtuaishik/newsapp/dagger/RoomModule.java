package com.kirtuaishik.newsapp.dagger;

import android.app.Application;

import androidx.room.Room;

import com.kirtuaishik.newsapp.dbase.ArticleDatabase;
import com.kirtuaishik.newsapp.dbase.ArticleTableRepo;
import com.kirtuaishik.newsapp.dbase.ArticlesDAO;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {
    private final ArticleDatabase articleDatabase;

    public RoomModule(Application application) {
        this.articleDatabase = Room.databaseBuilder(application, ArticleDatabase.class, "articles_database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    ArticleDatabase providesRoomDatabase() {
        return articleDatabase;
    }

    @Singleton
    @Provides
    ArticlesDAO providesProductDao(ArticleDatabase demoDatabase) {
        return demoDatabase.getArticlesDAO();
    }

    @Singleton
    @Provides
    ArticleTableRepo productRepository(ArticlesDAO productDao) {
        return new ArticleDataSource(productDao);
    }

}
