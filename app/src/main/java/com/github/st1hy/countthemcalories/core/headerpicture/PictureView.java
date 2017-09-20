package com.github.st1hy.countthemcalories.core.headerpicture;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import rx.Observable;

public interface PictureView {

    @NonNull
    @CheckResult
    Observable<Void> getSelectPictureObservable();

    @NonNull
    ImageView getImageView();
}
