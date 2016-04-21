package com.github.st1hy.countthemcalories.idling;

import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.github.st1hy.countthemcalories.core.rx.RxPicassoCallback.PicassoLoadingEvent;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RxPicassoIdlingResource {
    final CountingIdlingResource idlingResource = new CountingIdlingResource("Picasso loader");
    private final Subscription subscription;

    private RxPicassoIdlingResource(Observable<PicassoLoadingEvent> observable) {
        subscription = observable.observeOn(Schedulers.computation())
                .subscribe(new Action1<PicassoLoadingEvent>() {
                    @Override
                    public void call(PicassoLoadingEvent event) {
                        switch (event) {
                            case STARTED:
                                idlingResource.increment();
                                break;
                            case SUCCESS:
                                idlingResource.decrement();
                            case ERROR:
                                break;
                        }
                    }
                });
    }

    @NonNull
    public static RxPicassoIdlingResource registerAndGet(@NonNull Observable<PicassoLoadingEvent> observable) {
        RxPicassoIdlingResource rxPicassoIdlingResource = new RxPicassoIdlingResource(observable);
        Espresso.registerIdlingResources(rxPicassoIdlingResource.idlingResource);
        return rxPicassoIdlingResource;
    }

    public void unregister() {
        subscription.unsubscribe();
        Espresso.unregisterIdlingResources(idlingResource);
    }
}
