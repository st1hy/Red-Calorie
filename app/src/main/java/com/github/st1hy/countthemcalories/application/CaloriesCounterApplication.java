package com.github.st1hy.countthemcalories.application;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.application.inject.ApplicationModule;
import com.github.st1hy.countthemcalories.application.inject.DaggerApplicationComponent;
import com.github.st1hy.countthemcalories.database.application.DatabaseApplication;

import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
                @Override
                public void handleError(Throwable e) {
                    try {
                        Log.e("CRASH", "Unhandled exception", e);
                    } catch (Throwable t) {
                        //Required by error handler
                    }
                }
            });
        }
    }

    public static CaloriesCounterApplication get(@NonNull Context context) {
        return (CaloriesCounterApplication) context.getApplicationContext();
    }

    @VisibleForTesting
    public void setComponent(@NonNull ApplicationComponent component) {
        this.component = component;
    }
}
