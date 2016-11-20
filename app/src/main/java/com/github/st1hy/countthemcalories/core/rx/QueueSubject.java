package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Notification;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Actions;
import rx.subjects.Subject;

/**
 * Subject with internal queue for unreceived calls to each method.
 * When first subscriber subscribes queue if flushed to the subscriber.
 * If there are many subscriptions each receives the call to corresponding method.
 * Does not automatically unsubscribe in onComplete or onError.
 * Catches exceptions in onNext and onCompleted and calls onError when that happens.
 */
public class QueueSubject<T> extends Subject<T, T> {
    private final State<T> state;

    public QueueSubject(@NonNull State<T> state) {
        super(state);
        this.state = state;
    }

    public static <T> QueueSubject<T> create() {
        return new QueueSubject<>(new State<T>());
    }

    @Override
    public boolean hasObservers() {
        return state.hasObservers();
    }

    @Override
    public void onCompleted() {
        try {
            state.onCompleted();
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    public void onError(Throwable e) {
        state.onError(e);
    }

    @Override
    public void onNext(T t) {
        try {
            state.onNext(t);
        } catch (Exception e) {
            onError(e);
        }
    }

    public void doAfterDeliveryOnTerminate(@NonNull Action0 action) {
        state.doAfterDelivery = action;
    }

    static class State<T> implements OnSubscribe<T> {
        private final Queue<Notification<T>> queue = new ConcurrentLinkedQueue<>();
        private final List<Subscriber<? super T>> subscribers = new CopyOnWriteArrayList<>();
        private Action0 doAfterDelivery = Actions.empty();

        @Override
        public void call(final Subscriber<? super T> subscriber) {
            if (!subscriber.isUnsubscribed()) {
                while (!queue.isEmpty()) {
                    Notification<T> notification = queue.poll();
                    if (notification != null) {
                        notification.accept(subscriber);
                        if (notification.isOnCompleted() || notification.isOnError()) {
                            doAfterDelivery.call();
                        }
                    }
                }
                subscribers.add(subscriber);
                subscriber.add(new Subscription() {
                    transient boolean isUnsubscribed = false;

                    @Override
                    public void unsubscribe() {
                        subscribers.remove(subscriber);
                        isUnsubscribed = true;
                    }

                    @Override
                    public boolean isUnsubscribed() {
                        return isUnsubscribed;
                    }
                });
            }
        }

        public void onNext(T t) {
            if (!hasObservers()) {
                queue.add(Notification.createOnNext(t));
            } else {
                for (Subscriber<? super T> s : subscribers) {
                    if (!s.isUnsubscribed())
                        s.onNext(t);
                    else
                        subscribers.remove(s);
                }
            }
        }

        public void onError(Throwable e) {
            if (!hasObservers()) {
                queue.add(Notification.<T>createOnError(e));
            } else {
                for (Subscriber<? super T> s : subscribers) {
                    if (!s.isUnsubscribed())
                        s.onError(e);
                    else
                        subscribers.remove(s);
                }
                doAfterDelivery.call();
            }
        }

        public boolean hasObservers() {
            return !subscribers.isEmpty();
        }

        public void onCompleted() {
            if (!hasObservers()) {
                queue.add(Notification.<T>createOnCompleted());
            } else {
                for (Subscriber<? super T> s : subscribers) {
                    if (!s.isUnsubscribed())
                        s.onCompleted();
                    else
                        subscribers.remove(s);
                }
                doAfterDelivery.call();
            }
        }
    }
}
