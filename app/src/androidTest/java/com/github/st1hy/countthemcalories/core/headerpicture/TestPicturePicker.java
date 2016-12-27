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

    @DrawableRes
    public static final int TEST_CAMERA_PICTURE = android.R.drawable.ic_input_add;

    @NonNull
    private final PicturePickerImpl picturePicker;
    private HeaderPicturePickerUtils utils;

    @Inject
    public TestPicturePicker(@NonNull PicturePickerImpl picturePicker,
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

//    @Override
//    @NonNull
//    @CheckResult
//    public Observable.Transformer<ImageSource, Uri> pickImage() {
//        return (source) -> {
//            Observable<ImageSource> sharedSource = source.share();
//            return sharedSource.filter(Filters.equalTo(ImageSource.CAMERA))
//                    .map((s) -> resourceToUri(context, TEST_CAMERA_PICTURE))
//                    .mergeWith(
//                            sharedSource.filter((imageSource -> !imageSource.equals(ImageSource.CAMERA)))
//                                    .compose(picturePicker.pickImage())
//                    );
//        };
//    }


    @Override
    @NonNull
    @CheckResult
    public Observable.Transformer<ImageSource, Uri> pickImage() {
        return (source) -> {
            Observable<ImageSource> sharedSource = source.share();
            return sharedSource.compose(picturePicker.pickImage())
                    .mergeWith(
                            sharedSource.compose(utils.onPickImage(this))
                    );
        };
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        picturePicker.onSaveState(outState);
    }


    public void setTempImageUri(@Nullable Uri tempImageUri) {
        picturePicker.setTempImageUri(tempImageUri);
    }
}
