package com.github.st1hy.countthemcalories.core.withpicture.presenter;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.LoadedSource;
import com.github.st1hy.countthemcalories.core.withpicture.model.ImageSource;
import com.github.st1hy.countthemcalories.core.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate.from;
import static com.github.st1hy.countthemcalories.core.withpicture.model.ImageSource.intoImageSource;

public class WithPicturePresenterImp implements WithPicturePresenter {

    protected final PermissionsHelper permissionsHelper;
    protected final WithPictureView view;
    protected final WithPictureModel model;
    protected final CompositeSubscription subscriptions = new CompositeSubscription();
    private final ImageHolderDelegate imageHolderDelegate;
    private final Subject<Uri, Uri> loadUriSource = PublishSubject.create();
    private final Subject<Uri, Uri> deleteUriSource = PublishSubject.create();

    public WithPicturePresenterImp(@NonNull WithPictureView view,
                                   @NonNull PermissionsHelper permissionsHelper,
                                   @NonNull WithPictureModel model,
                                   @NonNull ImageHolderDelegate imageHolderDelegate) {
        this.permissionsHelper = permissionsHelper;
        this.view = view;
        this.model = model;
        this.imageHolderDelegate = imageHolderDelegate;
    }

    @Override
    public void onStart() {
        imageHolderDelegate.onAttached();
        subscriptions.add(view.getSelectPictureObservable()
                .flatMap(checkPermission())
                .filter(Permission.isGranted())
                .flatMap(showDialog())
                .map(intoImageSource())
                .subscribe(new SimpleSubscriber<ImageSource>() {
                    @Override
                    public void onNext(ImageSource imageSource) {
                        onSelectedImageSource(imageSource);
                    }
                }));
        subscriptions.add(view.getPictureSelectedObservable()
                .mergeWith(deleteUriSource)
                .doOnNext(new Action1<Uri>() {
                    @Override
                    public void call(Uri uri) {
                        onImageUriChanged(uri);
                    }
                })
                .mergeWith(loadUriSource)
                .subscribe(new SimpleSubscriber<Uri>() {
                    @Override
                    public void onNext(Uri uri) {
                        setImageUri(uri);
                    }
                }));
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

                return view.showAlertDialog(model.getImageSourceDialogTitleResId(), options);
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
            case REMOVE_SOURCE:
                deleteUriSource.onNext(Uri.EMPTY);
                break;
        }
    }

    /**
     * Called when image uri has changed.
     *
     * @param uri image uri
     */
    protected void onImageUriChanged(@NonNull final Uri uri) {
    }

    /**
     * Request image loading into internal image view.
     * Permissions will be checked if necessary.
     *
     * @param uri uri of the image. Null or Uri#EMPTY results in loading placeholder instead.
     */
    protected void loadImageUri(@Nullable final Uri uri) {
        loadUriSource.onNext(uri);
    }

    private void setImageUri(@Nullable Uri uri) {
        imageHolderDelegate.setImageUri(from(uri));
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
