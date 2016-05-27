package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;

import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class ViewTreeScrollChangedListener implements Observable.OnSubscribe<ViewScrollChangeEvent> {
    private final View view;

    public ViewTreeScrollChangedListener(@NonNull View view) {
        this.view = view;
    }

    @Override
    public void call(final Subscriber<? super ViewScrollChangeEvent> subscriber) {
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();

        final OnScrollChangedListener listener = new OnScrollChangedListener() {
            int oldX, oldY;

            @Override
            public void onScrollChanged() {
                if (!subscriber.isUnsubscribed()) {
                    int scrollX = view.getScrollX();
                    int scrollY = view.getScrollY();
                    subscriber.onNext(ViewScrollChangeEvent.create(view, scrollX, scrollY, oldX, oldY));
                    oldX = scrollX;
                    oldY = scrollY;
                }
            }
        };
        viewTreeObserver.addOnScrollChangedListener(listener);

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                viewTreeObserver.removeOnScrollChangedListener(listener);
            }
        });
    }

    @NonNull
    public static Observable<ViewScrollChangeEvent> create(@NonNull View view) {
        return Observable.create(new ViewTreeScrollChangedListener(view));
    }
}
