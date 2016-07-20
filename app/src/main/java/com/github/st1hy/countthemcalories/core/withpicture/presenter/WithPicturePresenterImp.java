package com.github.st1hy.countthemcalories.core.withpicture.presenter;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.withpicture.model.ImageSource;
import com.github.st1hy.countthemcalories.core.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;
import com.squareup.picasso.Picasso;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.withpicture.model.ImageSource.intoImageSource;

public class WithPicturePresenterImp implements WithPicturePresenter {

    protected final PermissionsHelper permissionsHelper;
    protected final WithPictureView view;
    protected final WithPictureModel model;
    protected final CompositeSubscription subscriptions = new CompositeSubscription();
    private final Picasso picasso;

    public WithPicturePresenterImp(@NonNull WithPictureView view,
                                   @NonNull PermissionsHelper permissionsHelper,
                                   @NonNull WithPictureModel model,
                                   @NonNull Picasso picasso) {
        this.permissionsHelper = permissionsHelper;
        this.view = view;
        this.model = model;
        this.picasso = picasso;
    }

    @Override
    public void onStart() {
        subscriptions.add(view.getSelectPictureObservable()
                .flatMap(checkPermission())
                .filter(Permission.isGranted())
                .flatMap(showDialog())
                .map(intoImageSource())
                .subscribe(new Action1<ImageSource>() {
                    @Override
                    public void call(ImageSource imageSource) {
                        onSelectedImageSource(imageSource);
                    }
                }));
        subscriptions.add(view.getPictureSelectedObservable()
                .doOnNext(new Action1<Uri>() {
                    @Override
                    public void call(Uri uri) {
                        onImageReceived(uri);
                    }
                })
                .flatMap(intoImageView())
                .filter(successfulLoading())
                .subscribe(new SimpleSubscriber<RxPicasso.PicassoEvent>()));
    }

    @Override
    public void onStop() {
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
                view.getImageView().setImageResource(model.getSelectImageDrawableRes());
                view.hideImageOverlay();
                onImageReceived(Uri.EMPTY);
                break;
        }
    }

    @NonNull
    private Func1<Uri, Observable<RxPicasso.PicassoEvent>> intoImageView() {
        return new Func1<Uri, Observable<RxPicasso.PicassoEvent>>() {
            @Override
            public Observable<RxPicasso.PicassoEvent> call(Uri uri) {
                return loadPictureObservable(uri);
            }
        };
    }

    @NonNull
    protected Observable<RxPicasso.PicassoEvent> loadPictureObservable(@NonNull Uri uri) {
        return RxPicasso.Builder.with(picasso, uri)
                .centerCrop()
                .fit()
                .into(view.getImageView())
                .asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<RxPicasso.PicassoEvent>() {
                    @Override
                    public void call(RxPicasso.PicassoEvent event) {
                        if (event == RxPicasso.PicassoEvent.SUCCESS) view.showImageOverlay();
                    }
                });
    }

    @NonNull
    private Func1<RxPicasso.PicassoEvent, Boolean> successfulLoading() {
        return new Func1<RxPicasso.PicassoEvent, Boolean>() {
            @Override
            public Boolean call(RxPicasso.PicassoEvent event) {
                return event == RxPicasso.PicassoEvent.SUCCESS;
            }
        };
    }

    protected void onImageReceived(@NonNull final Uri uri) {
    }

}
