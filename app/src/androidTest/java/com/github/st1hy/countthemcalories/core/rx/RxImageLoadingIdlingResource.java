package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;

public class RxImageLoadingIdlingResource {
    final CountingIdlingResource idlingResource = new CountingIdlingResource("ImageLoader");

    private RxImageLoadingIdlingResource() {
        RxImageLoader.Hook.setHook(new RxImageLoader.Hook() {
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
    public static RxImageLoadingIdlingResource registerAndGet() {
        RxImageLoadingIdlingResource rxImageLoadingIdlingResource = new RxImageLoadingIdlingResource();
        Espresso.registerIdlingResources(rxImageLoadingIdlingResource.idlingResource);
        return rxImageLoadingIdlingResource;
    }

    public void unregister() {
        RxImageLoader.Hook.setHook(null);
        Espresso.unregisterIdlingResources(idlingResource);
    }
}
