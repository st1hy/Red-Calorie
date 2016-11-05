package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.ImageView;

import java.util.Collection;

import rx.Observable;

public abstract class AddIngredientScreenDelegate implements AddIngredientScreen {

    protected abstract AddIngredientScreen getDelegate();

    @Override
    public void setResultAndFinish(@NonNull Intent intent) {
        getDelegate().setResultAndFinish(intent);
    }

    @Override
    public void openSelectTagScreen(@NonNull Collection<String> tagNames) {
        getDelegate().openSelectTagScreen(tagNames);
    }

    @Override
    @NonNull
    public Observable<Void> getSaveObservable() {
        return getDelegate().getSaveObservable();
    }

    @Override
    public void showInWebBrowser(@NonNull Uri address) {
        getDelegate().showInWebBrowser(address);
    }

    @Override
    public ImageView getImageView() {
        return getDelegate().getImageView();
    }

    @Override
    public void openCameraAndGetPicture() {
        getDelegate().openCameraAndGetPicture();
    }

    @Override
    public void pickImageFromGallery() {
        getDelegate().pickImageFromGallery();
    }

    @Override
    @NonNull
    public Observable<Void> getSelectPictureObservable() {
        return getDelegate().getSelectPictureObservable();
    }

    @Override
    @NonNull
    public Observable<Uri> getPictureSelectedObservable() {
        return getDelegate().getPictureSelectedObservable();
    }

    @Override
    public void showImageOverlay() {
        getDelegate().showImageOverlay();
    }

    @Override
    public void hideImageOverlay() {
        getDelegate().hideImageOverlay();
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, @ArrayRes int optionsRes) {
        return getDelegate().showAlertDialog(titleRes, optionsRes);
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, CharSequence[] options) {
        return getDelegate().showAlertDialog(titleRes, options);
    }
}
