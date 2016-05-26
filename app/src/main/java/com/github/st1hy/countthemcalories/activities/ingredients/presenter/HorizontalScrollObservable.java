package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class HorizontalScrollObservable {

    private static final Func1<MotionEvent, Integer> EVENT_ACTION = new Func1<MotionEvent, Integer>() {
        @Override
        public Integer call(MotionEvent motionEvent) {
            return motionEvent.getAction();
        }
    };
    private static final Func1<Integer, Boolean> IS_TOUCHING = new Func1<Integer, Boolean>() {

        @Override
        public Boolean call(Integer motionAction) {
            return motionAction != MotionEvent.ACTION_UP;
        }
    };
    private static final Func1<Boolean, Boolean> NOT_TOUCHING = new Func1<Boolean, Boolean>() {
        @Override
        public Boolean call(Boolean isTouching) {
            return !isTouching;
        }
    };

    @NonNull
    public static Observable<ViewScrollChangeEvent> create(final View scrollView) {
        final AtomicBoolean isTouching = new AtomicBoolean(true);
        final AtomicReference<ViewScrollChangeEvent> lastScrollEvent = new AtomicReference<>();

        return RxView.touches(scrollView)
                .doOnNext(new Action1<MotionEvent>() {
                    @Override
                    public void call(MotionEvent motionEvent) {
                        scrollView.onTouchEvent(motionEvent);
                    }
                })
                .map(EVENT_ACTION)
                .distinctUntilChanged()
                .map(IS_TOUCHING)
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        isTouching.set(aBoolean);
                    }
                })
                .filter(NOT_TOUCHING)
                .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
                .switchMap(new Func1<Boolean, Observable<ViewScrollChangeEvent>>() {
                    @Override
                    public Observable<ViewScrollChangeEvent> call(Boolean aBoolean) {
                        ViewScrollChangeEvent viewScrollChangeEvent = lastScrollEvent.getAndSet(null);
                        if (viewScrollChangeEvent != null) {
                            return Observable.just(viewScrollChangeEvent);
                        } else {
                            return Observable.empty();
                        }
                    }
                })
                .mergeWith(RxView.scrollChangeEvents(scrollView)
                        .debounce(50, TimeUnit.MILLISECONDS)
                        .switchMap(new Func1<ViewScrollChangeEvent, Observable<ViewScrollChangeEvent>>() {
                            @Override
                            public Observable<ViewScrollChangeEvent> call(ViewScrollChangeEvent viewScrollChangeEvent) {
                                lastScrollEvent.set(viewScrollChangeEvent);
                                if (!isTouching.get()) {
                                    return Observable.just(viewScrollChangeEvent);
                                } else {
                                    return Observable.empty();
                                }
                            }
                        })
                )
                .distinctUntilChanged();
    }
}
