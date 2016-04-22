package com.github.st1hy.countthemcalories.core.rx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test no exception thrown during execution (i.e no  UnsupportedOperationException)
 * Mostly just for coverage.
 */
@RunWith(JUnit4.class)
public class SimpleObserverTest {
    private final SimpleObserver<Object> observer = new SimpleObserver<>();

    @Test
    public void testOnCompleted() {
        observer.onCompleted();
    }

    @Test
    public void testOnError() {
        observer.onError(null);
        observer.onError(new Error());
    }

    @Test
    public void testOnNext() {
        observer.onNext(null);
        observer.onNext(new Object());
    }
}