package com.github.st1hy.countthemcalories.activities.withpicture;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WithPicturePresenterImp implements WithPicturePresenter {
    protected final PermissionsHelper permissionsHelper;
    protected final WithPictureView view;

    public WithPicturePresenterImp(@NonNull WithPictureView view,
                                   @NonNull PermissionsHelper permissionsHelper) {
        this.permissionsHelper = permissionsHelper;
        this.view = view;
    }

    @Override
    public void onImageClicked() {
        permissionsHelper.checkPermissionAndAskIfNecessary(Manifest.permission.READ_EXTERNAL_STORAGE, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission == Permission.GRANTED) {
                            view.showSelectImageInputDialog();
                        }
                    }
                });
    }

    @Override
    public void onSelectedImageSource(@NonNull ImageSource imageSource) {
        switch (imageSource) {
            case CAMERA:
                view.openCameraAndGetPicture();
                break;
            case GALLERY:
                view.pickImageFromGallery();
                break;
        }
    }

    @Override
    public void onImageReceived(@NonNull final Uri uri) {
        view.setImageToView(uri);
    }

    @Override
    public void onImageLoadingSuccess() {
    }

    @Override
    public void onImageLoadingFailed() {

    }
}
