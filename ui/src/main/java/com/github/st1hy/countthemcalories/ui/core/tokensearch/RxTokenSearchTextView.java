package com.github.st1hy.countthemcalories.ui.core.tokensearch;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.core.tokensearch.TokenSearchTextView.DropDownDialogChangeListener;

import rx.Observable;
import rx.android.MainThreadSubscription;

public final class RxTokenSearchTextView {

    private RxTokenSearchTextView() {
    }

    @NonNull
    @CheckResult
    public static Observable<Boolean> dropDownDialogChange(@NonNull TokenSearchTextView tokenSearchTextView) {
        return Observable.unsafeCreate(subscriber -> {
            tokenSearchTextView.setOnDropDownDialogChangeListener(new DropDownDialogChangeListener() {
                @Override
                public void onDialogShown() {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(true);
                    }
                }

                @Override
                public void onDialogDismissed() {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(false);
                    }
                }
            });
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    tokenSearchTextView.setOnDropDownDialogChangeListener(null);
                }
            });
        });
    }
}
