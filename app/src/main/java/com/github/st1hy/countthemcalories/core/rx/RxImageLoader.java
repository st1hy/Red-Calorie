package com.github.st1hy.countthemcalories.core.rx;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.st1hy.countthemcalories.R;

import rx.Observable;
import rx.android.MainThreadSubscription;
import rx.functions.Action1;

public class RxImageLoader {
    private final Context context;
    private final ImageView imageView;
    private final Uri uri;
    private final RequestListener<Drawable> requestListener;

    private Action1<ImageLoadingEvent> delegate;
    private ImageLoadingEvent lastEvent;
    private Observable<ImageLoadingEvent> observable;

    private RxImageLoader(Builder builder) {
        this.imageView = builder.imageView;
        this.context = builder.context;
        this.uri = builder.uri;
        this.requestListener = new RequestListener<Drawable>() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e,
                                        Object model,
                                        Target<Drawable> target,
                                        boolean isFirstResource) {
                onError();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource,
                                           Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                onSuccess();
                return false;
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
            observable = Observable.unsafeCreate((Observable.OnSubscribe<ImageLoadingEvent>) subscriber -> {

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
        Glide.with(context).clear(imageView);
    }

    public static class Builder {
        private final RequestBuilder<Drawable> request;
        private RequestOptions options;
        private final Uri uri;
        private final Context context;
        private ImageView imageView;

        private Builder(RequestBuilder<Drawable> request, Uri uri, Context context) {
            this.request = request;
            this.uri = uri;
            this.context = context;
            this.options = new RequestOptions();
        }

        public RequestBuilder<Drawable> childBuilder() {
            return request;
        }

        public static Builder with(@NonNull Context context, @NonNull Uri uri) {
            return new Builder(
                    Glide.with(context)
                            .load(uri),
                    uri, context);
        }

        public Builder centerCrop() {
            options = options.centerCrop();
            return this;
        }

        public Builder fitCenter() {
            options = options.fitCenter();
            return this;
        }

        public Builder placeholder(@DrawableRes int resId) {
            options = options.placeholder(resId);
            return this;
        }

        public RxImageLoader into(@NonNull ImageView imageView) {
            imageView.setTag(R.id.image_tag_uri, uri);
            this.imageView = imageView;
            RxImageLoader rxImageLoader = new RxImageLoader(this);
            Hook.onStarted();
            request.apply(options).listener(rxImageLoader.requestListener).into(imageView);
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