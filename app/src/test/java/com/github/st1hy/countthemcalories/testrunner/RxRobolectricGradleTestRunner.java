package com.github.st1hy.countthemcalories.testrunner;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;

public class RxRobolectricGradleTestRunner extends RobolectricGradleTestRunner {

    public RxRobolectricGradleTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
//        TestRxPlugins.registerImmediateHookIO();
    }
}
