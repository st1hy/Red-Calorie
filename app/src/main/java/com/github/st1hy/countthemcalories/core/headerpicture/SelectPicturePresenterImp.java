package com.github.st1hy.countthemcalories.core.headerpicture;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.LoadedSource;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.headerpicture.ImageSource.intoImageSource;
import static com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate.from;

public final class SelectPicturePresenterImp implements SelectPicturePresenter {

    private final PermissionsHelper permissionsHelper;
    private final PictureView view;
    private final DialogView dialogView;
    private final PicturePicker pictureController;
    private final PictureModel model;
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final ImageHolderDelegate imageHolderDelegate;
    private final Subject<Uri, Uri> internalUriSource = PublishSubject.create();

    @Inject
    public SelectPicturePresenterImp(@NonNull PictureView pictureView,
                                     @NonNull PermissionsHelper permissionsHelper,
                                     @NonNull DialogView dialogView,
                                     @NonNull PicturePicker pictureController,
                                     @NonNull PictureModel model,
                                     @NonNull @Named("header") ImageHolderDelegate imageHolderDelegate) {
        this.permissionsHelper = permissionsHelper;
        this.view = pictureView;
        this.dialogView = dialogView;
        this.pictureController = pictureController;
        this.model = model;
        this.imageHolderDelegate = imageHolderDelegate;
    }

    @Override
    public void onStart() {
        imageHolderDelegate.onAttached();
        subscriptions.add(
                view.getSelectPictureObservable()
                        .flatMap(checkPermission())
                        .filter(Permission.isGranted())
                        .flatMap(showDialog())
                        .map(intoImageSource())
                        .compose(pictureController.pickImage())
                        .doOnNext(new Action1<Uri>() {
                            @Override
                            public void call(Uri uri) {
                                model.setImageUri(uri);
                            }
                        })
                        .mergeWith(internalUriSource)
                        .subscribe(new SimpleSubscriber<Uri>() {
                            @Override
                            public void onNext(Uri uri) {
                                displayImageUri(uri);
                            }
                        })
        );
        subscriptions.add(imageHolderDelegate.getLoadingObservable()
                .subscribe(new SimpleSubscriber<LoadedSource>() {
                    @Override
                    public void onNext(LoadedSource loadedSource) {
                        showOverlay(loadedSource);
                    }
                }));
    }

    @Override
    public void onStop() {
        imageHolderDelegate.onDetached();
        subscriptions.clear();
    }

    @NonNull
    private Func1<Void, Observable<Permission>> checkPermission() {
        return new Func1<Void, Observable<Permission>>() {
            @Override
            public Observable<Permission> call(Void aVoid) {
                return permissionsHelper.checkPermissionAndAskIfNecessary(Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
            }
        };
    }

    @NonNull
    private Func1<Permission, Observable<Integer>> showDialog() {
        return new Func1<Permission, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Permission permission) {
                @ArrayRes int options = (model.hasImage()) ?
                        model.getSelectImageSourceAndRemoveOptions() :
                        model.getSelectImageSourceOptions();

                return dialogView.showAlertDialog(model.getImageSourceDialogTitleResId(), options);
            }
        };
    }

    /**
     * Request image loading into internal image view.
     * Permissions will be checked if necessary.
     *
     * @param uri uri of the image. Null or Uri#EMPTY results in loading placeholder instead.
     */
    @Override
    public void loadImageUri(@Nullable final Uri uri) {
        internalUriSource.onNext(uri);
    }

    private void displayImageUri(@Nullable Uri uri) {
        imageHolderDelegate.displayImage(from(uri));
    }

    private void showOverlay(@NonNull LoadedSource loadedSource) {
        switch (loadedSource) {
            case PICASSO:
                view.showImageOverlay();
                break;
            case PLACEHOLDER:
                view.hideImageOverlay();
                break;
        }
    }

}
