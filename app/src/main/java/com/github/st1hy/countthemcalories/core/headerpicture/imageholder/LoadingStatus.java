package com.github.st1hy.countthemcalories.core.headerpicture.imageholder;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.RxImageLoader;

public enum LoadingStatus {
    PLACEHOLDER, URI_SUCCESS, URI_FAILED;

    @NonNull
    static LoadingStatus convert(RxImageLoader.ImageLoadingEvent event) {
        switch (event) {
            case SUCCESS:
                return LoadingStatus.URI_SUCCESS;
            case ERROR:
                return LoadingStatus.URI_FAILED;
            default:
                throw new UnsupportedOperationException();
        }
    }

}
