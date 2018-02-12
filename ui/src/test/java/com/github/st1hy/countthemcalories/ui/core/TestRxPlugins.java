package com.github.st1hy.countthemcalories.ui.core;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;

public class TestRxPlugins {

    public static void registerImmediateMainThreadHook() {
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return rx.schedulers.Schedulers.immediate();
            }
        });
    }

}
