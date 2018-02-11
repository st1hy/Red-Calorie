package com.github.st1hy.countthemcalories.ui.core.dialog;

import android.support.annotation.ArrayRes;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import rx.Observable;

public interface DialogView {

    @NonNull
    @CheckResult
    Observable<Integer> showAlertDialog(@StringRes int titleRes, @ArrayRes int optionsRes);

    @NonNull
    @CheckResult
    Observable<Integer> showAlertDialog(@StringRes int titleRes, CharSequence[] options);

}
