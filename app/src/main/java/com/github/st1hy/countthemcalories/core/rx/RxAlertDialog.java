package com.github.st1hy.countthemcalories.core.rx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class RxAlertDialog {
    private AlertDialog dialog;
    private final OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (positiveClickedDelegate != null)
                        positiveClickedDelegate.onClick(dialog, which);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    if (negativeClickedDelegate != null)
                        negativeClickedDelegate.onClick(dialog, which);
                    break;
                default:
                    if (itemClickedDelegate != null)
                        itemClickedDelegate.onClick(dialog, which);
            }
        }
    };
    private OnClickListener itemClickedDelegate;
    private OnClickListener positiveClickedDelegate;
    private OnClickListener negativeClickedDelegate;
    private final OnCancelListener onCancel = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            if (onCancelDelegate != null) onCancelDelegate.onCancel(dialog);
        }
    };
    private OnCancelListener onCancelDelegate;
    private View customView;

    @Nullable
    public View getCustomView() {
        return customView;
    }

    private RxAlertDialog() {
    }

    @NonNull
    public Observable<Integer> observeItemClick() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(final Subscriber<? super Integer> subscriber) {
                itemClickedDelegate = new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onNext(which);
                    }
                };
                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        itemClickedDelegate = null;
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @NonNull
    public Observable<Void> observeNegativeClick() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                negativeClickedDelegate = new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onNext(null);
                    }
                };
                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        negativeClickedDelegate = null;
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @NonNull
    public Observable<Void> observePositiveClick() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                positiveClickedDelegate = new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onNext(null);
                    }
                };
                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        positiveClickedDelegate = null;
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @NonNull
    public Observable<Void> observeCanceled() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                onCancelDelegate = new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onNext(null);
                    }
                };
                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        onCancelDelegate = null;
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
        private final Context context;
        private final AlertDialog.Builder builder;
        @ArrayRes
        int items = -1;
        CharSequence[] itemsArray = null;
        @LayoutRes
        int layoutId = -1;
        @StringRes
        int positiveButtonName = -1;
        @StringRes
        int negativeButtonName = -1;

        public static RxAlertDialog.Builder with(@NonNull Context context) {
            return new Builder(context);
        }

        private Builder(@NonNull Context context) {
            this.context = context;
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
            this.items = items;
            return this;
        }

        @NonNull
        public RxAlertDialog.Builder items(@NonNull CharSequence[] items) {
            this.itemsArray = items;
            return this;
        }

        @NonNull
        public RxAlertDialog.Builder customView(@LayoutRes int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        @NonNull
        public RxAlertDialog.Builder positiveButton(@StringRes int name) {
            this.positiveButtonName = name;
            return this;
        }

        public RxAlertDialog.Builder negativeButton(@StringRes int name) {
            this.negativeButtonName = name;
            return this;
        }

        public RxAlertDialog.Builder message(@StringRes int messageId) {
            builder.setMessage(messageId);
            return this;
        }

        @NonNull
        public RxAlertDialog show() {
            RxAlertDialog rxAlertDialog = new RxAlertDialog();
            if (items != -1) {
                builder.setItems(items, rxAlertDialog.clickListener);
            }
            if (itemsArray != null) {
                builder.setItems(itemsArray, rxAlertDialog.clickListener);
            }
            if (layoutId != -1) {
                View view = LayoutInflater.from(context).inflate(layoutId, null);
                rxAlertDialog.customView = view;
                builder.setView(view);
            }
            if (positiveButtonName != -1) {
                builder.setPositiveButton(positiveButtonName, rxAlertDialog.clickListener);
            }
            if (negativeButtonName != -1) {
                builder.setNegativeButton(negativeButtonName, rxAlertDialog.clickListener);
            }
            builder.setOnCancelListener(rxAlertDialog.onCancel);
            rxAlertDialog.dialog = builder.show();
            return rxAlertDialog;
        }

    }
}
