package com.github.st1hy.countthemcalories.core.headerpicture;

import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.WithState;

import rx.Observable;


public interface PicturePicker extends WithState {

    String SAVE_TEMP_URI = "with picture temp uri";

    @NonNull
    @CheckResult
    Observable.Transformer<ImageSource, Uri> pickImage();
}
