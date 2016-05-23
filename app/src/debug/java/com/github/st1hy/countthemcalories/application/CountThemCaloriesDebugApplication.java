package com.github.st1hy.countthemcalories.application;

import android.os.StrictMode;

import com.codemonkeylabs.fpslibrary.TinyDancer;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

public class CountThemCaloriesDebugApplication extends CaloriesCounterApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
        LeakCanary.install(this);
        TinyDancer.create()
                .startingXPosition(200)
                .startingYPosition(200)
                .show(this);
        Timber.plant(new Timber.DebugTree());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }
}
