package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;

public class RxPicassoIdlingResource {
    final CountingIdlingResource idlingResource = new CountingIdlingResource("Picasso loader");

    private RxPicassoIdlingResource() {
        RxPicasso.Hook.setHook(new RxPicasso.Hook() {
            @Override
            public void onImageLoadingStarted() {
                idlingResource.increment();
            }

            @Override
            public void onImageLoadingFinished() {
                idlingResource.decrement();
            }
        });
    }

    @NonNull
    public static RxPicassoIdlingResource registerAndGet() {
        RxPicassoIdlingResource rxPicassoIdlingResource = new RxPicassoIdlingResource();
        Espresso.registerIdlingResources(rxPicassoIdlingResource.idlingResource);
        return rxPicassoIdlingResource;
    }

    public void unregister() {
        RxPicasso.Hook.setHook(null);
        Espresso.unregisterIdlingResources(idlingResource);
    }
}
