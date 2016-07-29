package com.github.st1hy.countthemcalories.core.withpicture;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.permissions.Permission.GRANTED;

public class ImageHolderDelegate {

    final Picasso picasso;
    final PermissionsHelper permissionsHelper;
    final ImageView imageView;

    final Subject<Optional<Uri>, Optional<Uri>> imageUriSubject = BehaviorSubject.create();
    final CompositeSubscription subscriptions = new CompositeSubscription();

    @DrawableRes
    int placeholderResId = R.drawable.ic_fork_and_knife_wide;
    //Lazy
    private Func1<Optional<Uri>, Observable<?>> imageViewLoader;
    //Lazy
    private Func1<Optional<Uri>, Observable<Optional<Uri>>> permissionChecker;

    public ImageHolderDelegate(@NonNull Picasso picasso,
                               @NonNull PermissionsHelper permissionsHelper,
                               @NonNull ImageView imageView) {
        this.picasso = picasso;
        this.permissionsHelper = permissionsHelper;
        this.imageView = imageView;
    }

    public void onAttached() {
        subscriptions.add(loadImage().subscribe());
    }

    public void onDetached() {
        subscriptions.clear();
    }

    /**
     * Sets imageView to display this uri. If uri is absent sets imageView to display placeholder
     *
     * @param uri optional uri
     */
    public void setImageUri(@NonNull Optional<Uri> uri) {
        imageUriSubject.onNext(uri);
    }

    /**
     * Changes placeholder resource to be loaded in #setImageUri
     *
     * @param placeholderResId drawable respource id
     */
    public void setImagePlaceholder(@DrawableRes int placeholderResId) {
        this.placeholderResId = placeholderResId;
    }

    @NonNull
    private Observable<Void> loadImage() {
        return imageUriSubject.switchMap(checkPermission())
                .switchMap(intoImageView())
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
    private Func1<Optional<Uri>, Observable<?>> intoImageView() {
        if (imageViewLoader == null) {
            imageViewLoader = new Func1<Optional<Uri>, Observable<?>>() {
                @Override
                public Observable<?> call(Optional<Uri> uri) {
                    if (uri.isPresent()) {
                        return RxPicasso.Builder.with(picasso, uri.get())
                                .placeholder(placeholderResId)
                                .centerCrop()
                                .fit()
                                .into(imageView)
                                .asObservable();
                    } else {
                        imageView.setImageResource(placeholderResId);
                        return Observable.empty();
                    }
                }
            };
        }
        return imageViewLoader;
    }
}
