package com.github.st1hy.countthemcalories.activities.withpicture;

import android.net.Uri;
import android.support.annotation.NonNull;

public interface WithPicturePresenter {

    void onImageClicked();

    void onSelectedImageSource(@NonNull ImageSource imageSource);

    void onImageReceived(@NonNull Uri data);

    void onImageLoadingSuccess();

    void onImageLoadingFailed();
}
