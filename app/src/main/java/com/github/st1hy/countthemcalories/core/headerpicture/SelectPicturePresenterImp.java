package com.github.st1hy.countthemcalories.core.headerpicture;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;

import javax.inject.Inject;
import javax.inject.Named;

import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.headerpicture.ImageSource.intoImageSource;

public final class SelectPicturePresenterImp implements SelectPicturePresenter {

    private final PermissionsHelper permissionsHelper;
    private final PictureView view;
    private final DialogView dialogView;
    private final PicturePicker pictureController;
    private final PictureModel model;
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final ImageHolderDelegate imageHolderDelegate;
    private final Subject<Uri, Uri> internalUriSource = BehaviorSubject.create();

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
                internalUriSource.mergeWith(
                        view.getSelectPictureObservable()
                                .flatMap(aVoid ->
                                        permissionsHelper.checkPermissionAndAskIfNecessary(
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE, null)
                                )
                                .filter(Permission.isGranted())
                                .flatMap(permission ->
                                        dialogView.showAlertDialog(
                                                model.getImageSourceDialogTitleResId(), getOptions()
                                        )
                                )
                                .map(intoImageSource())
                                .compose(pictureController.pickImage())
                                .doOnNext(model::setImageUri)
                ).subscribe(imageHolderDelegate::displayImage)
        );
        subscriptions.add(
                imageHolderDelegate.getLoadingObservable()
                        .subscribe((loadedSource) -> {
                            switch (loadedSource) {
                                case PICASSO:
                                    view.showImageOverlay();
                                    break;
                                case PLACEHOLDER:
                                    view.hideImageOverlay();
                                    break;
                            }
                        })
        );
    }

    private int getOptions() {
        return (model.hasImage()) ?
                model.getSelectImageSourceAndRemoveOptions() :
                model.getSelectImageSourceOptions();
    }

    @Override
    public void onStop() {
        imageHolderDelegate.onDetached();
        subscriptions.clear();
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

}
