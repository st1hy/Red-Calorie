package com.github.st1hy.countthemcalories.core.headerpicture;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class PictureViewImpl implements PictureView {

    @BindView(R.id.image_header_image_view)
    ImageView ingredientImage;
    @BindView(R.id.image_header_overlay_top)
    View imageOverlayTop;
    @BindView(R.id.image_header_overlay_bottom)
    View imageOverlayBottom;

    public PictureViewImpl(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);
    }

    public PictureViewImpl(@NonNull Activity activity) {
        ButterKnife.bind(this, activity);
    }

    @Override
    public void showImageOverlay() {
        imageOverlayBottom.setVisibility(View.VISIBLE);
        imageOverlayTop.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideImageOverlay() {
        imageOverlayBottom.setVisibility(View.GONE);
        imageOverlayTop.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public Observable<Void> getSelectPictureObservable() {
        return RxView.clicks(ingredientImage);
    }
}
