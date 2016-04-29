package com.github.st1hy.countthemcalories.core.rx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class RxAlertDialog implements DialogInterface.OnClickListener {
    private AlertDialog dialog;
    private final Subject<Integer, Integer> subjectClick = PublishSubject.<Integer>create().toSerialized();

    private RxAlertDialog() {
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        subjectClick.onNext(which);
    }

    @NonNull
    public Observable<Integer> observeItemClick() {
        return subjectClick.asObservable();
    }


    @NonNull
    public AlertDialog getDialog() {
        return dialog;
    }

    public static class Builder {
        private final AlertDialog.Builder builder;
        private final RxAlertDialog rxAlertDialog = new RxAlertDialog();

        public static RxAlertDialog.Builder with(@NonNull Context context) {
            return new Builder(context);
        }

        private Builder(@NonNull Context context) {
            builder = new AlertDialog.Builder(context);
        }

        @NonNull
        public AlertDialog.Builder childBuilder() {
            return builder;
        }

        @NonNull
        public RxAlertDialog.Builder title(@StringRes int title) {
            builder.setTitle(title);
            return this;
        }

        @NonNull
        public RxAlertDialog.Builder items(@ArrayRes int items) {
            builder.setItems(items, rxAlertDialog);
            return this;
        }

        @NonNull
        public RxAlertDialog.Builder items(@NonNull CharSequence[] items) {
            builder.setItems(items, rxAlertDialog);
            return this;
        }

        @NonNull
        public RxAlertDialog show() {
            rxAlertDialog.dialog = builder.show();
            return rxAlertDialog;
        }

    }
}
