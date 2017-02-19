package com.github.st1hy.countthemcalories.core.dialog;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;

import rx.Observable;

public enum DialogEvent {
    POSITIVE, NEGATIVE, NEUTRAL, DISMISS, CANCEL;


}
