package com.github.st1hy.countthemcalories.core.headerpicture;

import android.net.Uri;
import android.support.annotation.CheckResult;

import com.github.st1hy.countthemcalories.core.WithState;

import rx.Observable;


public interface PicturePicker extends WithState {

    String SAVE_TEMP_URI = "with picture temp uri";

    @CheckResult
    Observable<Uri> openCameraAndGetPicture();

    @CheckResult
    Observable<Uri> pickImageFromGallery();

}
