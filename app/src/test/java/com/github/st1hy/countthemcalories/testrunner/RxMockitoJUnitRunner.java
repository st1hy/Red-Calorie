package com.github.st1hy.countthemcalories.testrunner;

import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;

import rx.plugins.TestRxPlugins;

public class RxMockitoJUnitRunner extends MockitoJUnitRunner {

    public RxMockitoJUnitRunner(Class<?> klass) throws InvocationTargetException {
        super(klass);
        TestRxPlugins.registerImmediateHook();
    }
}
