package com.github.st1hy.countthemcalories.application;

import android.app.Application;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.application.inject.ApplicationModule;
import com.github.st1hy.countthemcalories.application.inject.DaggerApplicationComponent;

import timber.log.Timber;

public class CaloriesCounterApplication extends Application {
    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        if (com.github.st1hy.countthemcalories.BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        component.inject(this);
    }


    @NonNull
    public ApplicationComponent getComponent() {
        return component;
    }
}
