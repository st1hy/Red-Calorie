package com.github.st1hy.countthemcalories.core.dialog;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;

import javax.inject.Inject;

import rx.Observable;

@PerActivity
public class DialogViewController implements DialogView {

    private final Context context;

    @Inject
    public DialogViewController(@ActivityContext Context context) {
        this.context = context;
    }

    @NonNull
    @CheckResult
    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, @ArrayRes int options) {
        return RxAlertDialog.Builder.with(context)
                .title(titleRes)
                .items(options)
                .show()
                .observeItemClick();
    }

    @NonNull
    @CheckResult
    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, CharSequence[] options) {
        return RxAlertDialog.Builder.with(context)
                .title(titleRes)
                .items(options)
                .show()
                .observeItemClick();
    }
}
