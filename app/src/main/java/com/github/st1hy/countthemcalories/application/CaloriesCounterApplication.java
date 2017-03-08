package com.github.st1hy.countthemcalories.application;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatDelegate;

import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.application.inject.DaggerApplicationComponent;
import com.github.st1hy.countthemcalories.database.application.DatabaseApplication;
import com.github.st1hy.countthemcalories.inject.common.ApplicationModule;

public class CaloriesCounterApplication extends DatabaseApplication {
    private ApplicationComponent component;

    @NonNull
    public ApplicationComponent getComponent() {
        if (component == null) {
            synchronized (CaloriesCounterApplication.class) {
                if (component == null) {
                    component = DaggerApplicationComponent.builder()
                            .applicationModule(new ApplicationModule(this))
                            .build();
                }
            }
        }
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static CaloriesCounterApplication get(@NonNull Context context) {
        return (CaloriesCounterApplication) context.getApplicationContext();
    }

    @VisibleForTesting
    public void setComponent(@NonNull ApplicationComponent component) {
        this.component = component;
    }
}
