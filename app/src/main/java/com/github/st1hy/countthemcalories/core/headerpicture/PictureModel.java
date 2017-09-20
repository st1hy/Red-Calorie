package com.github.st1hy.countthemcalories.core.headerpicture;

import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface PictureModel {

    @StringRes
    int getImageSourceDialogTitleResId();

    @ArrayRes
    int getSelectImageSourceOptions();

    @ArrayRes
    int getSelectImageSourceAndRemoveOptions();

    boolean hasImage();

    void setImageAvailableOverride(boolean isAvailable);

    void setImageUri(@NonNull Uri uri);
}
