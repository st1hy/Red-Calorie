package com.github.st1hy.countthemcalories.application;

import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

public class DevToolsDebugApplication extends DebugApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        Timber.d("Starting");
        LeakCanary.install(this);
        Timber.d("Finished devtools setup");
    }

}
