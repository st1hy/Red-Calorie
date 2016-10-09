package com.github.st1hy.countthemcalories.application;

import android.os.StrictMode;

import timber.log.Timber;

public class DebugApplication extends CaloriesCounterApplication {

    @Override
    protected void init() {
        super.init();
        Timber.plant(new Timber.DebugTree());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }
}
