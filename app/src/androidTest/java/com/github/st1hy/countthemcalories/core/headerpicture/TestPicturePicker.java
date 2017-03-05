package com.github.st1hy.countthemcalories.core.headerpicture;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import rx.Observable;

public class TestPicturePicker implements PicturePicker {

    @NonNull
    private final PicturePickerImpl picturePicker;
    private HeaderPicturePickerUtils utils;

    @Inject
    TestPicturePicker(@NonNull PicturePickerImpl picturePicker,
                             @NonNull HeaderPicturePickerUtils utils) {
        this.picturePicker = picturePicker;
        this.utils = utils;
    }

    public static Uri resourceToUri(Context context, @DrawableRes int resID) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(context.getPackageName())
                .path(String.valueOf(resID))
                .build();
    }

    @Override
    @NonNull
    @CheckResult
    public Observable.Transformer<ImageSource, Uri> pickImage() {
        return (source) -> {
            Observable<ImageSource> sharedSource = source.share();
            return sharedSource.compose(utils.onPickImage(this))
                    .mergeWith(
                            sharedSource.compose(picturePicker.pickImage())
                    );
        };
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        picturePicker.onSaveState(outState);
    }

    void setTempImageUri(@Nullable Uri tempImageUri) {
        picturePicker.setTempImageUri(tempImageUri);
    }
}
