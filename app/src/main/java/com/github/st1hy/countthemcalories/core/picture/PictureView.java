package com.github.st1hy.countthemcalories.core.picture;

import android.support.annotation.NonNull;

import rx.Observable;

public interface PictureView {

    @NonNull
    Observable<Void> getSelectPictureObservable();

    void showImageOverlay();

    void hideImageOverlay();
}
