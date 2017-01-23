package com.github.st1hy.countthemcalories.core.headerpicture.imageholder;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.RxImageLoader;

import javax.inject.Inject;

import rx.Observable;

/**
 * Doesn't use placeholder when loading image; so that when switching between one image and another
 * placeholder is not showed in mean time.
 */
public class WithoutPlaceholderImageHolderDelegate extends ImageHolderDelegate {

    @Inject
    public WithoutPlaceholderImageHolderDelegate() {
        super();
    }

    @NonNull
    @Override
    protected Observable<RxImageLoader.ImageLoadingEvent> loadImage(@NonNull Uri uri) {
        return RxImageLoader.Builder.with(appContext, uri)
                .centerCrop()
                .into(imageViewProvider.get())
                .asObservable();
    }
}
