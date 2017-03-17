package com.github.st1hy.countthemcalories.core.rx;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Action1;

public class RxInputMethodManager {

    @CheckResult
    @NonNull
    public static Observable<Integer> showSoftInput(InputMethodManager manager, View view, int flags) {
        return Observable.create(subscriber -> {
            AtomicReference<Action1<Integer>> ref = new AtomicReference<>((t) -> {
                subscriber.onNext(t);
                subscriber.onCompleted();
            });
            manager.showSoftInput(view, flags, new MResultReceiver(ref, subscriber));
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    ref.set(null);
                }
            });
        });
    }

    private static class MResultReceiver extends ResultReceiver {
        private final AtomicReference<Action1<Integer>> actionRef;
        private final Subscriber<? super Integer> subscriber;

        public MResultReceiver(AtomicReference<Action1<Integer>> actionRef,
                               Subscriber<? super Integer> subscriber) {
            super(null);
            this.actionRef = actionRef;
            this.subscriber = subscriber;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            Action1<Integer> action = actionRef.get();
            if (!subscriber.isUnsubscribed() && action != null) {
                action.call(resultCode);
            }
        }
    }
}
