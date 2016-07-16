package com.github.st1hy.countthemcalories.application;

import com.codemonkeylabs.fpslibrary.TinyDancer;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

public class DevToolsDebugApplication extends DebugApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("Starting");
        Stetho.initializeWithDefaults(this);
        LeakCanary.install(this);
        TinyDancer.create()
                .startingXPosition(500)
                .startingYPosition(10)
                .show(this);
        Timber.d("Finished devtools setup");

    }
}
