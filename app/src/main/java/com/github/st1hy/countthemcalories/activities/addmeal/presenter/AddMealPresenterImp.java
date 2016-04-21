package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;

import javax.inject.Inject;

public class AddMealPresenterImp implements AddMealPresenter {
    private final AddMealView view;

    @Inject
    public AddMealPresenterImp(AddMealView view) {
        this.view = view;
    }

    @Override
    public void onSaveButtonClicked() {
        view.openOverviewActivity();
    }

    @Override
    public void onImageClicked() {
        view.showSelectImageInputDialog();
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
    public void onImageReceived(@NonNull Uri uri) {
        view.setImageToView(uri);
    }

    @Override
    public void onImageLoadingSuccess() {

    }

    @Override
    public void onImageLoadingFailed() {

    }
}
