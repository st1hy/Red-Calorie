package com.github.st1hy.countthemcalories.core.ui.withpicture.presenter;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.ui.withpicture.model.ImageSource;
import com.github.st1hy.countthemcalories.core.ui.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.core.ui.withpicture.view.WithPictureView;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.ui.withpicture.model.ImageSource.intoImageSource;

public class WithPicturePresenterImp implements WithPicturePresenter {
    protected final PermissionsHelper permissionsHelper;
    protected final WithPictureView view;
    protected final WithPictureModel model;
    protected final CompositeSubscription subscriptions = new CompositeSubscription();

    public WithPicturePresenterImp(@NonNull WithPictureView view,
                                   @NonNull PermissionsHelper permissionsHelper,
                                   @NonNull WithPictureModel model) {
        this.permissionsHelper = permissionsHelper;
        this.view = view;
        this.model = model;
    }

    @Override
    public void onImageClicked() {
        permissionsHelper.checkPermissionAndAskIfNecessary(Manifest.permission.READ_EXTERNAL_STORAGE, null)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(Permission.isGranted())
                .flatMap(showDialog())
                .map(intoImageSource())
                .subscribe(new Action1<ImageSource>() {
                    @Override
                    public void call(ImageSource imageSource) {
                        onSelectedImageSource(imageSource);
                    }
                });
    }

    public void onStop() {
        subscriptions.unsubscribe();
    }

    @NonNull
    private Func1<Permission, Observable<Integer>> showDialog() {
        return new Func1<Permission, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Permission permission) {
                return view.showAlertDialog(
                        model.getImageSourceDialogTitleResId(),
                        model.getImageSourceOptionArrayResId()
                );
            }
        };
    }

    void onSelectedImageSource(@NonNull ImageSource imageSource) {
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
        Subscription subscription = view.showImage(uri)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        subscriptions.add(subscription);
    }

}
