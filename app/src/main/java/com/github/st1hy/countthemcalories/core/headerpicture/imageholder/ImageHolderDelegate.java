package com.github.st1hy.countthemcalories.core.headerpicture.imageholder;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso.PicassoEvent;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;

import javax.inject.Provider;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.permissions.Permission.GRANTED;

/**
 * Wraps image loading using picasso and permission checking for reading external storage.
 * <p/>
 * If Picasso cannot load image, permission is missing or if provided image is absent,
 * placeholder is used instead.
 */
public class ImageHolderDelegate {

    @NonNull
    protected final Picasso picasso;
    @NonNull
    protected final PermissionsHelper permissionsHelper;
    @NonNull
    protected final Provider<ImageView> imageViewProvider;

    private final Subject<Optional<Uri>, Optional<Uri>> imageUriSubject = BehaviorSubject.create();
    private final Subject<LoadedSource, LoadedSource> loadedSourceSubject = PublishSubject.create();
    private final Observable<LoadedSource> loadedSourceObservable = loadedSourceSubject.asObservable();
    private final CompositeSubscription subscriptions = new CompositeSubscription();


    @DrawableRes
    private int placeholderResId = R.drawable.ic_fork_and_knife_wide;

    public ImageHolderDelegate(@NonNull Picasso picasso,
                               @NonNull PermissionsHelper permissionsHelper,
                               @NonNull Provider<ImageView> imageViewProvider) {
        this.picasso = picasso;
        this.permissionsHelper = permissionsHelper;
        this.imageViewProvider = imageViewProvider;
    }

    @NonNull
    public static Optional<Uri> from(@Nullable Uri uri) {
        return uri == null || Uri.EMPTY.equals(uri) ? Optional.absent() : Optional.of(uri);
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
    public void displayImage(@NonNull Optional<Uri> uri) {
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
                    if (uri.isPresent()) {
                        return loadImage(uri.get());
                    } else {
                        return Observable.just(PicassoEvent.ERROR);
                    }
                })
                .doOnNext((picassoEvent) -> {
                    switch (picassoEvent) {
                        case SUCCESS:
                            loadedSourceSubject.onNext(LoadedSource.PICASSO);
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
    private Func1<Optional<Uri>, Observable<Optional<Uri>>> checkPermission() {
        return uri -> {
            if (uri.isPresent()) return permissionsHelper
                    .checkPermissionAndAskOnce(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .map(permission -> permission == GRANTED ? uri : Optional.absent());
            else return Observable.just(uri);
        };
    }

    /**
     * Implementation of image loading with RxPicasso
     *
     * @param uri not null image uri
     * @return observable for image loading
     */
    @NonNull
    protected Observable<PicassoEvent> loadImage(@NonNull Uri uri) {
        return RxPicasso.Builder.with(picasso, uri)
                .placeholder(placeholderResId)
                .centerCrop()
                .fit()
                .into(imageViewProvider.get())
                .asObservable();
    }

    @NonNull
    public Observable<LoadedSource> getLoadingObservable() {
        return loadedSourceObservable;
    }

}
