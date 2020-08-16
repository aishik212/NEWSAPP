package com.kirtuaishik.newsapp.dagger;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class baseApp extends DaggerApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
