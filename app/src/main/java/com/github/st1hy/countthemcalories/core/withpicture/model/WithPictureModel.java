package com.github.st1hy.countthemcalories.core.withpicture.model;

import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;

public abstract class WithPictureModel {

    @StringRes
    public abstract int getImageSourceDialogTitleResId();

    @ArrayRes
    public abstract int getSelectImageSourceOptions();

    @ArrayRes
    public abstract int getSelectImageSourceAndRemoveOptions();

    public abstract boolean hasImage();

    @DrawableRes
    public int getSelectImageDrawableRes() {
        return R.drawable.ic_add_a_photo_24px;
    }
}
