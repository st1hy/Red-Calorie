package com.github.st1hy.countthemcalories.ui.testrunner;

import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;

import com.github.st1hy.countthemcalories.ui.core.TestRxPlugins;

public class RxMockitoJUnitRunner extends MockitoJUnitRunner {

    public RxMockitoJUnitRunner(Class<?> klass) throws InvocationTargetException {
        super(klass);
        TestRxPlugins.registerImmediateMainThreadHook();
    }
}
