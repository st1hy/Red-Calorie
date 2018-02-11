package com.github.st1hy.countthemcalories.ui.core.viewcontrol;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import com.github.st1hy.countthemcalories.ui.core.Utils;
import com.github.st1hy.countthemcalories.ui.core.rx.Functions;
import com.github.st1hy.countthemcalories.ui.core.rx.ViewTreeScrollChangedListener;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.functions.Func1;

public class HorizontalScrollObservable {

    private static final Func1<Integer, Boolean> IS_TOUCHING = motionAction -> motionAction != MotionEvent.ACTION_UP && motionAction != MotionEvent.ACTION_CANCEL;
    private static final Func1<Boolean, Boolean> NOT_TOUCHING = isTouching1 -> !isTouching1;

    private final AtomicBoolean isTouching = new AtomicBoolean(true);
    private final AtomicReference<ViewScrollChangeEvent> lastScrollEvent = new AtomicReference<>();

    private final View scrollView;
    private final View touchOverlay;
    private final Observable<ViewScrollChangeEvent> observable;
    private final Observable<Void> idleObservable;
    private final Utils utils = new Utils();

    private HorizontalScrollObservable(@NonNull View scrollView, @NonNull View touchOverlay) {
        this.scrollView = scrollView;
        this.touchOverlay = touchOverlay;
        Observable<Boolean> touchingObservable = getTouchingObservable().share();
        observable = touchingObservable.filter(NOT_TOUCHING)
                .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
                .switchMap(isTouching -> {
                    ViewScrollChangeEvent viewScrollChangeEvent = lastScrollEvent.getAndSet(null);
                    if (viewScrollChangeEvent != null) {
                        return Observable.just(viewScrollChangeEvent);
                    } else {
                        return Observable.empty();
                    }
                })
                .mergeWith(getScrollingObservable()
                        .switchMap(object1 -> {
                            if (!isTouching.get()) {
                                return Observable.just(object1);
                            } else {
                                return Observable.empty();
                            }
                        })
                )
                .distinctUntilChanged();
        idleObservable = touchingObservable.filter(NOT_TOUCHING)
                .throttleWithTimeout(1500, TimeUnit.MILLISECONDS)
                .map(Functions.INTO_VOID)
                .switchMap(object -> {
                    if (!isTouching.get()) {
                        return Observable.just(object);
                    } else {
                        return Observable.empty();
                    }
                });
    }

    @NonNull
    public static HorizontalScrollObservable create(@NonNull View scrollView) {
        return new HorizontalScrollObservable(scrollView, scrollView);
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public Observable<ViewScrollChangeEvent> getScrollToPositionObservable() {
        return observable;
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public Observable<Void> getIdleObservable() {
        return idleObservable;
    }

    @NonNull
    private Observable<Boolean> getTouchingObservable() {
        return RxView.touches(touchOverlay)
                .doOnNext(touchOverlay::onTouchEvent)
                .map(MotionEvent::getAction)
                .distinctUntilChanged()
                .map(IS_TOUCHING)
                .doOnNext(isTouching::set);
    }

    @NonNull
    @CheckResult
    private Observable<ViewScrollChangeEvent> getScrollingObservable() {
        Observable<ViewScrollChangeEvent> observable;
        if (utils.hasMarshmallow()) {
            observable = RxView.scrollChangeEvents(scrollView);
        } else {
            observable = ViewTreeScrollChangedListener.create(scrollView);
        }
        return observable.debounce(50, TimeUnit.MILLISECONDS)
                .doOnNext(lastScrollEvent::set);
    }

}
