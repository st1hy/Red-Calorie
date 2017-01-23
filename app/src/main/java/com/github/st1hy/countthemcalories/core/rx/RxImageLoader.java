package com.github.st1hy.countthemcalories.core.rx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.st1hy.countthemcalories.R;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Action1;

public class RxImageLoader {
    private final ImageView imageView;
    private final Uri uri;
    private final BitmapImageViewTarget target;

    private Action1<ImageLoadingEvent> delegate;
    private ImageLoadingEvent lastEvent;
    private Observable<ImageLoadingEvent> observable;

    private RxImageLoader(Builder builder) {
        this.imageView = builder.imageView;
        this.uri = builder.uri;
        this.target = new BitmapImageViewTarget(imageView) {


            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                super.onResourceReady(resource, glideAnimation);
                onSuccess();
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                onError();
            }
        };
    }

    public void onSuccess() {
        lastEvent = ImageLoadingEvent.SUCCESS;
        if (delegate != null) delegate.call(lastEvent);
        Hook.onFinished();
    }

    public void onError() {
        lastEvent = ImageLoadingEvent.ERROR;
        if (delegate != null) delegate.call(lastEvent);
        Hook.onFinished();
    }

    public enum ImageLoadingEvent {
        SUCCESS, ERROR
    }

    @NonNull
    public Observable<ImageLoadingEvent> asObservable() {
        if (observable == null) {
            observable = Observable.create(new Observable.OnSubscribe<ImageLoadingEvent>() {
                @Override
                public void call(final Subscriber<? super ImageLoadingEvent> subscriber) {

                    delegate = event -> {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(event);
                            subscriber.onCompleted();
                        }
                    };
                    subscriber.add(new MainThreadSubscription() {
                        @Override
                        protected void onUnsubscribe() {
                            delegate = null;
                            if (!hasFinished() && isLoadingMyUri()) {
                                cancelRequest();
                            }
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


    private boolean isLoadingMyUri() {
        return uri.equals(imageView.getTag(R.id.image_tag_uri));
    }

    private boolean hasFinished() {
        return lastEvent != null;
    }

    private void cancelRequest() {
        Glide.clear(target);
    }

    public static class Builder {
        private final BitmapTypeRequest<Uri> request;
        private final Uri uri;
        private ImageView imageView;

        private Builder(BitmapTypeRequest<Uri> request, Uri uri) {
            this.request = request;
            this.uri = uri;
        }

        public BitmapTypeRequest<Uri> childBuilder() {
            return request;
        }

        public static Builder with(@NonNull Context context, @NonNull Uri uri) {
            return new Builder(
                    Glide.with(context)
                            .load(uri)
                            .asBitmap(),
                    uri);
        }

        public Builder centerCrop() {
            request.centerCrop();
            return this;
        }

        public Builder fitCenter() {
            request.fitCenter();
            return this;
        }

        public Builder placeholder(@DrawableRes int resId) {
            request.placeholder(resId);
            return this;
        }

        public RxImageLoader into(@NonNull ImageView imageView) {
            imageView.setTag(R.id.image_tag_uri, uri);
            this.imageView = imageView;
            RxImageLoader rxImageLoader = new RxImageLoader(this);
            Hook.onStarted();
            request.into(rxImageLoader.target);
            return rxImageLoader;
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