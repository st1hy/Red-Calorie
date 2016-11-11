package com.github.st1hy.countthemcalories.core.headerpicture.imageholder;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso.PicassoEvent;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;

import javax.inject.Provider;

import rx.Observable;
import rx.functions.Action1;
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

    protected final Picasso picasso;
    protected final PermissionsHelper permissionsHelper;
    protected final Provider<ImageView> imageViewProvider;

    private final Subject<Optional<Uri>, Optional<Uri>> imageUriSubject = BehaviorSubject.create();
    private final Subject<LoadedSource, LoadedSource> loadedSourceSubject = PublishSubject.create();
    private final Observable<LoadedSource> loadedSourceObservable = loadedSourceSubject.asObservable();
    private final CompositeSubscription subscriptions = new CompositeSubscription();


    @DrawableRes
    private int placeholderResId = R.drawable.ic_fork_and_knife_wide;
    //Lazy
    private Func1<Optional<Uri>, Observable<PicassoEvent>> imageViewLoader;
    //Lazy
    private Func1<Optional<Uri>, Observable<Optional<Uri>>> permissionChecker;
    private final Action1<PicassoEvent> ON_PICASSO_EVENT = new Action1<PicassoEvent>() {
        @Override
        public void call(PicassoEvent event) {
            onPicassoResult(event);
        }
    };

    public ImageHolderDelegate(@NonNull Picasso picasso,
                               @NonNull PermissionsHelper permissionsHelper,
                               @NonNull Provider<ImageView> imageViewProvider) {
        this.picasso = picasso;
        this.permissionsHelper = permissionsHelper;
        this.imageViewProvider = imageViewProvider;
    }

    @NonNull
    public static Optional<Uri> from(@Nullable Uri uri) {
        return uri == null || Uri.EMPTY.equals(uri) ? Optional.<Uri>absent() : Optional.of(uri);
    }

    public void onAttached() {
        subscriptions.add(loadImage().subscribe(new SimpleSubscriber<Void>()));
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
                .switchMap(intoImageView())
                .doOnNext(ON_PICASSO_EVENT)
                .map(Functions.INTO_VOID);
    }

    @NonNull
    private Func1<Optional<Uri>, Observable<Optional<Uri>>> checkPermission() {
        if (permissionChecker == null) {
            permissionChecker = new Func1<Optional<Uri>, Observable<Optional<Uri>>>() {
                @Override
                public Observable<Optional<Uri>> call(final Optional<Uri> uri) {
                    if (uri.isPresent()) return permissionsHelper
                            .checkPermissionAndAskOnce(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .map(intoUriIfGranted(uri));
                    else return Observable.just(uri);
                }
            };
        }
        return permissionChecker;
    }

    @NonNull
    private Func1<Permission, Optional<Uri>> intoUriIfGranted(final Optional<Uri> uri) {
        return new Func1<Permission, Optional<Uri>>() {
            @Override
            public Optional<Uri> call(Permission permission) {
                return permission == GRANTED ? uri : Optional.<Uri>absent();
            }
        };
    }

    @NonNull
    private Func1<Optional<Uri>, Observable<PicassoEvent>> intoImageView() {
        if (imageViewLoader == null) {
            imageViewLoader = new Func1<Optional<Uri>, Observable<PicassoEvent>>() {
                @Override
                public Observable<PicassoEvent> call(Optional<Uri> uri) {
                    if (uri.isPresent()) {
                        return loadImage(uri.get());
                    } else {
                        return Observable.just(PicassoEvent.ERROR);
                    }
                }
            };
        }
        return imageViewLoader;
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

    /**
     * Called after each attempt to load image.
     * This implementation shows placeholder image if error happens.
     *
     * @param picassoEvent success if loaded uri or error otherwise
     */
    @CallSuper
    protected void onPicassoResult(@NonNull PicassoEvent picassoEvent) {
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
    }

    @NonNull
    public Observable<LoadedSource> getLoadingObservable() {
        return loadedSourceObservable;
    }

}
