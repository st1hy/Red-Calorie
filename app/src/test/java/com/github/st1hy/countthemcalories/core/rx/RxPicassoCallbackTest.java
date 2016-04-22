package com.github.st1hy.countthemcalories.core.rx;

import com.github.st1hy.countthemcalories.core.rx.RxPicassoCallback.PicassoLoadingEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.schedulers.Schedulers;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class RxPicassoCallbackTest {

    private final RxPicassoCallback callback = new RxPicassoCallback();

    @Test
    public void testOnStarted() throws Exception {
        callback.onStarted();
        RxPicassoTestObserver testObserver = new RxPicassoTestObserver(PicassoLoadingEvent.STARTED, 1);
        callback.intoObservable()
                .observeOn(Schedulers.immediate())
                .subscribe();
        assertFalse(testObserver.isCompleted);
    }

    @Test
    public void testOnSuccess() throws Exception {
        callback.onSuccess();
        RxPicassoTestObserver testObserver = new RxPicassoTestObserver(PicassoLoadingEvent.SUCCESS, 1);
        callback.intoObservable()
                .observeOn(Schedulers.immediate())
                .subscribe();
        assertFalse(testObserver.isCompleted);
    }

    @Test
    public void testOnError() throws Exception {
        callback.onError();
        RxPicassoTestObserver testObserver = new RxPicassoTestObserver(PicassoLoadingEvent.ERROR, 1);
        callback.intoObservable()
                .observeOn(Schedulers.immediate())
                .subscribe();
        assertFalse(testObserver.isCompleted);
    }

    private static class RxPicassoTestObserver implements Observer<PicassoLoadingEvent> {
        private boolean isCompleted = false;
        private List<PicassoLoadingEvent> events = new ArrayList<>(1);
        private final PicassoLoadingEvent expect;
        private final int expectedEvents;

        private RxPicassoTestObserver(PicassoLoadingEvent expect, int expectedEvents) {
            this.expect = expect;
            this.expectedEvents = expectedEvents;
        }

        @Override
        public void onCompleted() {
            if (!isCompleted) {
                isCompleted = true;
            } else {
                throw new Error("on Complete called twice");
            }
            assertThat(events, hasSize(expectedEvents));
        }

        @Override
        public void onError(Throwable e) {
            throw new Error(e);
        }

        @Override
        public void onNext(PicassoLoadingEvent event) {
            assertEquals(expect, event);
            events.add(event);
            assertThat(events, hasSize(expectedEvents));
        }
    }
}