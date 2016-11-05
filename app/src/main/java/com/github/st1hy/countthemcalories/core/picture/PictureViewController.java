package com.github.st1hy.countthemcalories.core.picture;

import android.net.Uri;
import android.support.annotation.CheckResult;

import rx.Observable;


public interface PictureViewController {

    @CheckResult
    Observable<Uri> openCameraAndGetPicture();

    @CheckResult
    Observable<Uri> pickImageFromGallery();

}
