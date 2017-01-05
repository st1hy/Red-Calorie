package com.github.st1hy.countthemcalories.core.activityresult;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.SparseArray;

import com.github.st1hy.countthemcalories.core.rx.QueueSubject;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Provide a way to receive the results of the {@link Activity} with RxJava.
 */
@Singleton
public class RxActivityResult {

    private final SparseArray<QueueSubject<ActivityResult>> results = new SparseArray<>();

    @Inject
    public RxActivityResult() {
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        QueueSubject<ActivityResult> subject = results.get(requestCode);
        subject.onNext(new ActivityResult(requestCode, resultCode, data));
        subject.onCompleted();
    }

    void onError(int requestCode, ActivityNotFoundException exception) {
        results.get(requestCode).onError(exception);
    }

    Observable<ActivityResult> prepareSubject(final int requestCode) {
        QueueSubject<ActivityResult> subject = results.get(requestCode);
        if (subject == null) {
            subject = QueueSubject.create();
            subject.doAfterTerminate(() -> results.remove(requestCode));
        }
        results.put(requestCode, subject);
        return subject;
    }

    Observable<? extends ActivityResult> attachToExistingRequest(int requestCode) {
        Observable<ActivityResult> resultSubject = results.get(requestCode);
        if (resultSubject == null) resultSubject = Observable.empty();
        return resultSubject;
    }

}
