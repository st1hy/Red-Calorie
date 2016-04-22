package com.github.st1hy.countthemcalories.testrunner;

import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

public class RxMockitoJUnitRunner extends MockitoJUnitRunner {

    public RxMockitoJUnitRunner(Class<?> klass) throws InvocationTargetException {
        super(klass);
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }
}
