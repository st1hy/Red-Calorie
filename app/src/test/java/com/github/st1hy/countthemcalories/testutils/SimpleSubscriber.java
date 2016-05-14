package com.github.st1hy.countthemcalories.testutils;

import rx.Subscriber;

public class SimpleSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(T t) {

    }
}
