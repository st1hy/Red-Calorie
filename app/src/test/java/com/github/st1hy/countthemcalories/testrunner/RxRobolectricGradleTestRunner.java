package com.github.st1hy.countthemcalories.testrunner;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;

import rx.plugins.TestRxPlugins;

public class RxRobolectricGradleTestRunner extends RobolectricGradleTestRunner {

    public RxRobolectricGradleTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        TestRxPlugins.registerImmediateHook();
    }
}
