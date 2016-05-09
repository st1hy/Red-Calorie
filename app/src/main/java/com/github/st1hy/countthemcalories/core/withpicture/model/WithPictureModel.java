package com.github.st1hy.countthemcalories.core.withpicture.model;

import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;

public abstract class WithPictureModel {

    @StringRes
    public abstract int getImageSourceDialogTitleResId();

    @ArrayRes
    public abstract int getImageSourceOptionArrayResId();

}
