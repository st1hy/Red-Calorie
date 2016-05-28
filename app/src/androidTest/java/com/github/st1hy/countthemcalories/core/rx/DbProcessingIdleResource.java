package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.github.st1hy.countthemcalories.core.event.DbProcessing;

import rx.Subscriber;

public class DbProcessingIdleResource extends Subscriber<DbProcessing> {
    private CountingIdlingResource idlingResource = new CountingIdlingResource(DbProcessingIdleResource.class.getSimpleName());
    int count = 0;

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(DbProcessing dbProcessing) {
        if (dbProcessing == DbProcessing.STARTED && count == 0) {
            count++;
            idlingResource.increment();
        } else if (dbProcessing == DbProcessing.FINISHED && count == 1) {
            count--;
            idlingResource.decrement();
        }
    }

    @NonNull
    public IdlingResource getIdlingResource() {
        return idlingResource;
    }

    public void waitForStart() {
        if (count == 0) {
            count++;
            idlingResource.increment();
        }
    }
}
