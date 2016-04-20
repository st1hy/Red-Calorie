package com.github.st1hy.countthemcalories.application;

import android.app.Application;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.application.inject.ApplicationModule;
import com.github.st1hy.countthemcalories.application.inject.DaggerApplicationComponent;

import timber.log.Timber;

public class CaloriesCounterApplication extends Application {
    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        component.inject(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            StrictMode.setVmPolicy(component.getPolicyComponent().getVmPolicy());
            StrictMode.setThreadPolicy(component.getPolicyComponent().getThreadPolicy());
        }

    }

    @NonNull
    public ApplicationComponent getComponent() {
        return component;
    }

}
