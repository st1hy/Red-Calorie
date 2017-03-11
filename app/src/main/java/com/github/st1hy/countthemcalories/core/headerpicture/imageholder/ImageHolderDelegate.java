package com.github.st1hy.countthemcalories.core.headerpicture.imageholder;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.RxImageLoader;
import com.github.st1hy.countthemcalories.core.rx.RxImageLoader.ImageLoadingEvent;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.headerpicture.inject.HeaderImageView;
import com.github.st1hy.countthemcalories.inject.quantifier.context.AppContext;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.permissions.Permission.GRANTED;

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
    private final Subject<LoadedSource, LoadedSource> loadedSourceSubject = PublishSubject.create();
    private final Observable<LoadedSource> loadedSourceObservable = loadedSourceSubject.asObservable();
    private final CompositeSubscription subscriptions = new CompositeSubscription();

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
                        return Observable.just(ImageLoadingEvent.ERROR);
                    } else {
                        return loadImage(uri);
                    }
                })
                .doOnNext((imageLoadingEvent) -> {
                    switch (imageLoadingEvent) {
                        case SUCCESS:
                            loadedSourceSubject.onNext(LoadedSource.LOADED_SOURCE);
                            break;
                        case ERROR:
                        default:
                            imageViewProvider.get().setImageResource(placeholderResId);
                            loadedSourceSubject.onNext(LoadedSource.PLACEHOLDER);
                            break;
                    }
                })
                .map(Functions.INTO_VOID);
    }

    @NonNull
    private Func1<Uri, Observable<Uri>> checkPermission() {
        return uri -> {
            if (isEmpty(uri)) {
                return Observable.just(uri);
            } else {
                return permissionsHelper
                        .checkPermissionAndAskOnce(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .map(permission -> permission == GRANTED ? uri : Uri.EMPTY);
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
    protected Observable<ImageLoadingEvent> loadImage(@NonNull Uri uri) {
        return RxImageLoader.Builder.with(appContext, uri)
                .placeholder(placeholderResId)
                .centerCrop()
                .into(imageViewProvider.get())
                .asObservable();
    }

    @NonNull
    public Observable<LoadedSource> getLoadingObservable() {
        return loadedSourceObservable;
    }

    private static boolean isEmpty(@NonNull Uri uri) {
        return Uri.EMPTY.equals(uri);
    }

}
