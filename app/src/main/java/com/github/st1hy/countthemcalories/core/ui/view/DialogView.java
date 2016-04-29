package com.github.st1hy.countthemcalories.core.ui.view;

import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;

import rx.Observable;

public interface DialogView extends BaseView {

    Observable<Integer> showAlertDialog(@StringRes int titleRes, @ArrayRes int optionsRes);

    Observable<Integer> showAlertDialog(@StringRes int titleRes, CharSequence[] options);

}
