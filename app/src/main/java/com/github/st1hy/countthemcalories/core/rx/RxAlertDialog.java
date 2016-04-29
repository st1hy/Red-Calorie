package com.github.st1hy.countthemcalories.core.rx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class RxAlertDialog implements DialogInterface.OnClickListener {
    private AlertDialog dialog;
    private DialogInterface.OnClickListener delegate;

    private RxAlertDialog() {
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (delegate != null) delegate.onClick(dialog, which);
    }

    @NonNull
    public Observable<Integer> observeItemClick() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(final Subscriber<? super Integer> subscriber) {
                delegate = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onNext(which);
                    }
                };
                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        delegate = null;
                    }
                });
            }
        });
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
