package com.github.st1hy.countthemcalories.core.dialog;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

public class DialogViewController implements DialogView {

    private final Context context;

    @Inject
    public DialogViewController(@Named("activityContext") Context context) {
        this.context = context;
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, @ArrayRes int options) {
        return RxAlertDialog.Builder.with(context)
                .title(titleRes)
                .items(options)
                .show()
                .observeItemClick();
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, CharSequence[] options) {
        return RxAlertDialog.Builder.with(context)
                .title(titleRes)
                .items(options)
                .show()
                .observeItemClick();
    }
}
