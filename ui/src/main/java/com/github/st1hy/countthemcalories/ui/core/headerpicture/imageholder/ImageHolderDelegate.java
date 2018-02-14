package com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.HeaderImageView;
import com.github.st1hy.countthemcalories.ui.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.ui.core.rx.Functions;
import com.github.st1hy.countthemcalories.ui.core.rx.RxImageLoader;
import com.github.st1hy.countthemcalories.ui.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.AppContext;
import com.github.st1hy.countthemcalories.ui.core.permissions.Permission;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

/**
 * Wraps image loading using ImageLoader and permission checking for reading external storage.
 * <p/>
 * If ImageLoader cannot load image, permission is missing or if provided image is absent,
 * placeholder is used instead.
 */
public class ImageHolderDelegate {

    @Inject
    @AppContext
    Context appContext;
    @Inject
    PermissionsHelper permissionsHelper;
    @Inject
    @HeaderImageView
    Provider<ImageView> imageViewProvider;

    private final Subject<Uri, Uri> imageUriSubject = BehaviorSubject.create();
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final Subject<LoadingStatus, LoadingStatus> loadingStatusSubject = PublishSubject.create();

    @DrawableRes
    private int placeholderResId = R.drawable.ic_fork_and_knife_wide;

    @Inject
    public ImageHolderDelegate() {
    }

    public ImageHolderDelegate(@NonNull Context appContext,
                               @NonNull PermissionsHelper permissionsHelper,
                               @NonNull Provider<ImageView> imageViewProvider) {
        this.appContext = appContext;
        this.permissionsHelper = permissionsHelper;
        this.imageViewProvider = imageViewProvider;
    }

    public void onAttached() {
        subscriptions.add(loadImage().subscribe(new SimpleSubscriber<>()));
    }

    public void onDetached() {
        subscriptions.clear();
    }

    /**
     * Sets imageView to display this uri. If uri is absent sets imageView to display placeholder
     *
     * @param uri optional uri
     */
    public void displayImage(@NonNull Uri uri) {
        imageUriSubject.onNext(uri);
    }

    /**
     * Changes placeholder resource to be loaded in #displayImage
     *
     * @param placeholderResId drawable respource id
     */
    public void setImagePlaceholder(@DrawableRes int placeholderResId) {
        this.placeholderResId = placeholderResId;
    }

    @NonNull
    private Observable<Void> loadImage() {
        return imageUriSubject.distinctUntilChanged()
                .switchMap(checkPermission())
                .switchMap(uri -> {
                    if (isEmpty(uri)) {
                        return Observable.just(LoadingStatus.PLACEHOLDER);
                    } else {
                        return loadImage(uri).map(LoadingStatus::convert);
                    }
                })
                .doOnNext(loadingStatusSubject::onNext)
                .doOnNext((loadingStatus) -> {
                    switch (loadingStatus) {
                        case LoadingStatus.URI_FAILED:
                            onLoadingError();
                            // Fallthrough
                        case LoadingStatus.PLACEHOLDER:
                            imageViewProvider.get().setImageResource(placeholderResId);
                            break;
                    }
                })
                .map(Functions.INTO_VOID);
    }

    protected void onLoadingError() {
    }

    @NonNull
    private Func1<Uri, Observable<Uri>> checkPermission() {
        return uri -> {
            if (isEmpty(uri)) {
                return Observable.just(uri);
            } else {
                return permissionsHelper
                        .checkPermissionAndAskOnce(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .map(permission -> permission == Permission.GRANTED ? uri : Uri.EMPTY);
            }
        };
    }

    /**
     * Implementation of image loading with RxImageLoader
     *
     * @param uri not null image uri
     * @return observable for image loading
     */
    @NonNull
    protected Observable<RxImageLoader.ImageLoadingEvent> loadImage(@NonNull Uri uri) {
        return RxImageLoader.Builder.with(appContext, uri)
                .placeholder(placeholderResId)
                .centerCrop()
                .into(imageViewProvider.get())
                .asObservable();
    }

    @NonNull
    public Observable<LoadingStatus> getLoadingObservable() {
        return loadingStatusSubject;
    }

    private static boolean isEmpty(@NonNull Uri uri) {
        return Uri.EMPTY.equals(uri);
    }


}
