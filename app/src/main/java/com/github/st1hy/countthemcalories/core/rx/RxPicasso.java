package com.github.st1hy.countthemcalories.core.rx;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Action1;

public class RxPicasso implements Callback {
    private Action1<PicassoEvent> delegate;
    private ImageView imageView;
    private Picasso picasso;
    private PicassoEvent lastEvent;
    private Observable<PicassoEvent> observable;

    private RxPicasso(@NonNull Picasso picasso) {
        this.picasso = picasso;
    }

    @Override
    public void onSuccess() {
        lastEvent = PicassoEvent.SUCCESS;
        if (delegate != null) delegate.call(lastEvent);
        Hook.onFinished();
    }

    @Override
    public void onError() {
        lastEvent = PicassoEvent.ERROR;
        if (delegate != null) delegate.call(lastEvent);
        Hook.onFinished();
    }

    public enum PicassoEvent {
        SUCCESS, ERROR
    }

    @NonNull
    public Observable<PicassoEvent> asObservable() {
        if (observable == null) {
            observable = Observable.create(new Observable.OnSubscribe<PicassoEvent>() {
                @Override
                public void call(final Subscriber<? super PicassoEvent> subscriber) {

                    delegate = new Action1<PicassoEvent>() {
                        @Override
                        public void call(PicassoEvent event) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(event);
                                subscriber.onCompleted();
                            }
                        }
                    };
                    subscriber.add(new MainThreadSubscription() {
                        @Override
                        protected void onUnsubscribe() {
                            delegate = null;
                            picasso.cancelRequest(imageView);
                        }
                    });
                    if (lastEvent != null) {
                        delegate.call(lastEvent);
                    }
                }
            }).share();
        }
        return observable;
    }

    public static class Builder {
        private final RxPicasso rxPicasso;
        private final RequestCreator requestCreator;

        private Builder(RxPicasso rxPicasso, RequestCreator requestCreator) {
            this.rxPicasso = rxPicasso;
            this.requestCreator = requestCreator;
        }

        public RequestCreator childBuilder() {
            return requestCreator;
        }

        public static Builder with(@NonNull Picasso picasso, @NonNull Uri uri) {
            return new Builder(new RxPicasso(picasso), picasso.load(uri));
        }

        public Builder centerCrop() {
            requestCreator.centerCrop();
            return this;
        }

        public Builder fit() {
            requestCreator.fit();
            return this;
        }

        public Builder noFade() {
            requestCreator.noFade();
            return this;
        }

        public Builder placeholder(@DrawableRes int resId) {
            requestCreator.placeholder(resId);
            return this;
        }

        public RxPicasso into(@NonNull ImageView imageView) {
            rxPicasso.imageView = imageView;
            Hook.onStarted();
            requestCreator.into(imageView, rxPicasso);
            return rxPicasso;
        }
    }

    //For testing only
    abstract static class Hook {
        private static Hook hook;

        static void onStarted() {
            if (hook != null) hook.onImageLoadingStarted();
        }

        static void onFinished() {
            if (hook != null) hook.onImageLoadingFinished();
        }

        public static void setHook(@Nullable Hook hook) {
            Hook.hook = hook;
        }

        public abstract void onImageLoadingStarted();

        public abstract void onImageLoadingFinished();
    }
}