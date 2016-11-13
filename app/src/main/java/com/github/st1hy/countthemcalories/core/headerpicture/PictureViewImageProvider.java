package com.github.st1hy.countthemcalories.core.headerpicture;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import javax.inject.Inject;
import javax.inject.Provider;

public class PictureViewImageProvider implements Provider<ImageView> {

    @NonNull
    private final PictureView pictureView;

    @Inject
    public PictureViewImageProvider(@NonNull PictureView pictureView) {
        this.pictureView = pictureView;
    }

    @Override
    public ImageView get() {
        return pictureView.getImageView();
    }
}
