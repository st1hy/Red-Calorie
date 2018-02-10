package com.github.st1hy.countthemcalories.database.commands;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscriber;

public class UndoAvailability implements Observable.OnSubscribe<Boolean> {
    private AtomicBoolean isUndoAvailable;
    private Subscriber<? super Boolean> subscriber;

    UndoAvailability(boolean isUndoAvailable) {
        this.isUndoAvailable = new AtomicBoolean(isUndoAvailable);
    }

    boolean isUndoAvailable() {
        return isUndoAvailable.get();
    }

    void invalidate() {
        if (isUndoAvailable.getAndSet(false)) {
            if (subscriber != null && !subscriber.isUnsubscribed()) {
                subscriber.onNext(false);
                subscriber.onCompleted();
                subscriber = null;
            } else {
                subscriber = null;
            }
        }
    }

    @Override
    public void call(Subscriber<? super Boolean> subscriber) {
        this.subscriber = subscriber;
        if (!subscriber.isUnsubscribed()) {
            boolean isUndoAvailable = isUndoAvailable();
            subscriber.onNext(isUndoAvailable);
            if (!isUndoAvailable) {
                subscriber.onCompleted();
            }
        }
    }
}
