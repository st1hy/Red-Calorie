package com.github.st1hy.countthemcalories;

import android.app.Application;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.inject.component.ApplicationComponent;
import com.github.st1hy.countthemcalories.inject.component.DaggerApplicationComponent;
import com.github.st1hy.countthemcalories.inject.module.ApplicationModule;

import timber.log.*;

public class CaloriesCounterApplication extends Application {
    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG)
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
