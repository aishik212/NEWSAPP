package com.kirtuaishik.newsapp.dagger;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    @Provides
    static String API_KEY() {
        return "7876eea480474b59b25e82866d2f6374";
    }

    @Provides
    static Drawable logo(Application application) {
        return ContextCompat.getDrawable(application, android.R.drawable.presence_away);
    }

}
