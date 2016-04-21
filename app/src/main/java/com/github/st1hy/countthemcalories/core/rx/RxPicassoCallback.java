package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;

import com.squareup.picasso.Callback;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxPicassoCallback implements Callback {
    private final Subject<PicassoLoadingEvent, PicassoLoadingEvent> subject = new SerializedSubject<>(PublishSubject.<PicassoLoadingEvent>create());

    public void onStarted() {
        subject.onNext(PicassoLoadingEvent.STARTED);
    }

    @Override
    public void onSuccess() {
        subject.onNext(PicassoLoadingEvent.SUCCESS);
    }

    @Override
    public void onError() {
        subject.onNext(PicassoLoadingEvent.ERROR);
    }

    @NonNull
    public Observable<PicassoLoadingEvent> intoObservable() {
        return subject;
    }

    public enum PicassoLoadingEvent {
        STARTED, SUCCESS, ERROR
    }
}
