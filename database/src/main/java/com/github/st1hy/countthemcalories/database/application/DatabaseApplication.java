package com.github.st1hy.countthemcalories.database.application;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

public abstract class DatabaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(getApplicationContext());
    }
}
