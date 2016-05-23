package com.github.st1hy.countthemcalories.application;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.application.inject.ApplicationModule;
import com.github.st1hy.countthemcalories.application.inject.DaggerApplicationComponent;
import com.github.st1hy.countthemcalories.database.application.DatabaseApplication;

public class CaloriesCounterApplication extends DatabaseApplication {
    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        getComponent().inject(this);
    }


    @NonNull
    public ApplicationComponent getComponent() {
        if (component == null) {
            component = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return component;
    }

    public static CaloriesCounterApplication get(@NonNull Context context) {
        return (CaloriesCounterApplication) context.getApplicationContext();
    }

    //For testing
    public void setComponent(@NonNull ApplicationComponent component) {
        this.component = component;
    }
}
