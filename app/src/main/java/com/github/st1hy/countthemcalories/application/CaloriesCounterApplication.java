package com.github.st1hy.countthemcalories.application;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.github.st1hy.countthemcalories.database.application.DatabaseApplication;
import com.github.st1hy.countthemcalories.inject.application.ApplicationComponent;
import com.github.st1hy.countthemcalories.inject.application.ApplicationModule;
import com.github.st1hy.countthemcalories.inject.application.DaggerApplicationComponent;

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

    public static CaloriesCounterApplication get(@NonNull Context context) {
        return (CaloriesCounterApplication) context.getApplicationContext();
    }

    @VisibleForTesting
    public void setComponent(@NonNull ApplicationComponent component) {
        this.component = component;
    }
}
