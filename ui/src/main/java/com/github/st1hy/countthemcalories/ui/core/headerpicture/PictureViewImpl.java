package com.github.st1hy.countthemcalories.ui.core.headerpicture;

import android.app.Activity;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class PictureViewImpl implements PictureView {

    @BindView(R.id.image_header_image_view)
    ImageView ingredientImage;

    @Inject
    public PictureViewImpl(@NonNull Activity activity) {
        ButterKnife.bind(this, activity);
    }

    @NonNull
    @Override
    public ImageView getImageView() {
        return ingredientImage;
    }

    @CheckResult
    @NonNull
    @Override
    public Observable<Void> getSelectPictureObservable() {
        return RxView.clicks(ingredientImage);
    }
}
