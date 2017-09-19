package com.github.st1hy.countthemcalories.application;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

public class DevToolsDebugApplication extends DebugApplication {

    @Override
    protected void init() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        super.init();
        Timber.d("Starting");
        LeakCanary.install(this);
        Timber.d("Finished devtools setup");
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
