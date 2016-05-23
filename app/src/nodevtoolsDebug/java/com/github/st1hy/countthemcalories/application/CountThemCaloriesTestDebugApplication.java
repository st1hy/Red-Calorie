package com.github.st1hy.countthemcalories.application;

import timber.log.Timber;

public class CountThemCaloriesTestDebugApplication extends CaloriesCounterApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
