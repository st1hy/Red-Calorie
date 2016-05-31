package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class RxSnackbar {
    private final Snackbar snackbar;

    private RxSnackbar(Snackbar snackbar) {
        this.snackbar = snackbar;
    }

    @NonNull
    public static RxSnackbar make(@NonNull View root, @StringRes int titleRes, @Snackbar.Duration int duration) {
        return new RxSnackbar(Snackbar.make(root, titleRes, duration));
    }

    @NonNull
    public Observable<Void> action(@StringRes int actionResId) {
        OnSubscribeClickListener onClickListener = new OnSubscribeClickListener();
        snackbar.setAction(actionResId, onClickListener);
        return Observable.create(onClickListener);
    }

    @NonNull
    public Snackbar getSnackbar() {
        return snackbar;
    }

    public void show() {
        snackbar.show();
    }

    private static class OnSubscribeClickListener implements Observable.OnSubscribe<Void>, View.OnClickListener {
        private Subscriber<? super Void> subscriber;

        @Override
        public void call(Subscriber<? super Void> subscriber) {
            this.subscriber = subscriber;
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    OnSubscribeClickListener.this.subscriber = null;
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (subscriber != null && !subscriber.isUnsubscribed()) {
                subscriber.onNext(null);
            }
        }
    }

}
