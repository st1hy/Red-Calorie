package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

public class ObservableValue<T> {
    private T value;
    private final Subject<T, T> subject = BehaviorSubject.<T>create().toSerialized();

    public ObservableValue(@NonNull T value) {
        this.value = value;
        subject.onNext(value);
    }

    @NonNull
    public T getValue() {
        return value;
    }

    public void setValue(@NonNull T value) {
        this.value = value;
        subject.onNext(value);
    }

    public Observable<T> asObservable() {
        return subject;
    }
}
