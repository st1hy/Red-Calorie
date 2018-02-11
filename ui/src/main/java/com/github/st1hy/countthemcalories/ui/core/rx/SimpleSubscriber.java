package com.github.st1hy.countthemcalories.ui.core.rx;

import rx.Subscriber;
import timber.log.Timber;

public class SimpleSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Error not handled!");
    }

    @Override
    public void onNext(T t) {
    }
}
