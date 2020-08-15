package com.kirtuaishik.newsapp.DBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kirtuaishik.newsapp.models.Articles;

import java.util.List;

@Dao
public interface ArticlesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Articles articles);

    @Query("DELETE FROM articles_table")
    void deleteAll();

    @Query("SELECT * from articles_table order by date desc")
    LiveData<List<Articles>> getArticlesSorted();
}
