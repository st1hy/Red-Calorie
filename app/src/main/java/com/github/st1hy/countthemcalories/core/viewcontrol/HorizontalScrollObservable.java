package com.github.st1hy.countthemcalories.core.viewcontrol;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import com.github.st1hy.countthemcalories.core.Utils;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.ViewTreeScrollChangedListener;
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
            return motionAction != MotionEvent.ACTION_UP && motionAction != MotionEvent.ACTION_CANCEL;
        }
    };
    private static final Func1<Boolean, Boolean> NOT_TOUCHING = new Func1<Boolean, Boolean>() {
        @Override
        public Boolean call(Boolean isTouching) {
            return !isTouching;
        }
    };

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
                .switchMap(emitScrollEventIfExist())
                .mergeWith(getScrollingObservable()
                        .switchMap(this.<ViewScrollChangeEvent>emitIfNotTouching())
                )
                .distinctUntilChanged();
        idleObservable = touchingObservable.filter(NOT_TOUCHING)
                .throttleWithTimeout(1500, TimeUnit.MILLISECONDS)
                .map(Functions.INTO_VOID)
                .switchMap(this.<Void>emitIfNotTouching());
    }

    @NonNull
    public static HorizontalScrollObservable create(@NonNull View scrollView) {
        return new HorizontalScrollObservable(scrollView, scrollView);
    }

    @NonNull
    public Observable<ViewScrollChangeEvent> getScrollToPositionObservable() {
        return observable;
    }

    @NonNull
    public Observable<Void> getIdleObservable() {
        return idleObservable;
    }

    @NonNull
    private Observable<Boolean> getTouchingObservable() {
        return RxView.touches(touchOverlay)
                .doOnNext(loopBackToView(touchOverlay))
                .map(EVENT_ACTION)
                .distinctUntilChanged()
                .map(IS_TOUCHING)
                .doOnNext(setIsTouching());
    }

    @NonNull
    private Action1<MotionEvent> loopBackToView(final View view) {
        return new Action1<MotionEvent>() {
            @Override
            public void call(MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
            }
        };
    }

    @NonNull
    private Action1<Boolean> setIsTouching() {
        return new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                isTouching.set(aBoolean);
            }
        };
    }

    @NonNull
    private Func1<Boolean, Observable<ViewScrollChangeEvent>> emitScrollEventIfExist() {
        return new Func1<Boolean, Observable<ViewScrollChangeEvent>>() {
            @Override
            public Observable<ViewScrollChangeEvent> call(Boolean isTouching) {
                ViewScrollChangeEvent viewScrollChangeEvent = lastScrollEvent.getAndSet(null);
                if (viewScrollChangeEvent != null) {
                    return Observable.just(viewScrollChangeEvent);
                } else {
                    return Observable.empty();
                }
            }
        };
    }

    @NonNull
    private Observable<ViewScrollChangeEvent> getScrollingObservable() {
        Observable<ViewScrollChangeEvent> observable;
        if (utils.hasMarshmallow()) {
            observable = RxView.scrollChangeEvents(scrollView);
        } else {
            observable = ViewTreeScrollChangedListener.create(scrollView);
        }
        return observable.debounce(50, TimeUnit.MILLISECONDS)
                .doOnNext(setLastScrollEvent());
    }

    @NonNull
    private Action1<ViewScrollChangeEvent> setLastScrollEvent() {
        return new Action1<ViewScrollChangeEvent>() {
            @Override
            public void call(ViewScrollChangeEvent viewScrollChangeEvent) {
                lastScrollEvent.set(viewScrollChangeEvent);
            }
        };
    }

    @NonNull
    private <T> Func1<T, Observable<T>> emitIfNotTouching() {
        return new Func1<T, Observable<T>>() {
            @Override
            public Observable<T> call(T object) {
                if (!isTouching.get()) {
                    return Observable.just(object);
                } else {
                    return Observable.empty();
                }
            }
        };
    }
}
