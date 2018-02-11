package com.github.st1hy.countthemcalories.ui.core.rx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.support.annotation.ArrayRes;
import android.support.annotation.CheckResult;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;

import com.github.st1hy.countthemcalories.ui.core.dialog.DialogEvent;

import rx.Observable;
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
                case DialogInterface.BUTTON_NEUTRAL:
                    if (neutralClickedDelegate != null)
                        neutralClickedDelegate.onClick(dialog, which);
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
    private OnClickListener neutralClickedDelegate;
    private final OnCancelListener onCancel = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            if (onCancelDelegate != null) onCancelDelegate.onCancel(dialog);
        }
    };
    private OnCancelListener onCancelDelegate;
    private View customView;
    private final DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (onDismissListenerDelegate != null) onDismissListenerDelegate.onDismiss(dialog);
        }
    };
    private DialogInterface.OnDismissListener onDismissListenerDelegate;

    @NonNull
    @CheckResult
    public Observable<DialogEvent> basicEvents() {
        return observePositiveClick()
                .map(Functions.into(DialogEvent.POSITIVE))
                .mergeWith(observeNeutralClick().map(Functions.into(DialogEvent.NEUTRAL)))
                .mergeWith(observeNegativeClick().map(Functions.into(DialogEvent.NEGATIVE)))
                .mergeWith(observeCanceled().map(Functions.into(DialogEvent.CANCEL)))
                .mergeWith(observeDismiss().map(Functions.into(DialogEvent.DISMISS)));
    }

    @Nullable
    public View getCustomView() {
        return customView;
    }

    private RxAlertDialog() {
    }

    @NonNull
    @CheckResult
    public Observable<Integer> observeItemClick() {
        return Observable.unsafeCreate(subscriber -> {
            itemClickedDelegate = (dialog1, which) -> {
                if (!subscriber.isUnsubscribed())
                    subscriber.onNext(which);
            };
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    itemClickedDelegate = null;
                    dialog.dismiss();
                }
            });
        });
    }

    @SuppressWarnings("unused")
    @NonNull
    @CheckResult
    public Observable<Void> observeNegativeClick() {
        return Observable.unsafeCreate(subscriber -> {
            negativeClickedDelegate = (dialog1, which) -> {
                if (!subscriber.isUnsubscribed())
                    subscriber.onNext(null);
            };
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    negativeClickedDelegate = null;
                    dialog.dismiss();
                }
            });
        });
    }

    @NonNull
    @CheckResult
    public Observable<Void> observePositiveClick() {
        return Observable.unsafeCreate(subscriber -> {
            positiveClickedDelegate = (dialog1, which) -> {
                if (!subscriber.isUnsubscribed())
                    subscriber.onNext(null);
            };
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    positiveClickedDelegate = null;
                    dialog.dismiss();
                }
            });
        });
    }

    @NonNull
    @CheckResult
    public Observable<Void> observeNeutralClick() {
        return Observable.unsafeCreate(subscriber -> {
            neutralClickedDelegate = (dialog1, which) -> {
                if (!subscriber.isUnsubscribed())
                    subscriber.onNext(null);
            };
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    neutralClickedDelegate = null;
                    dialog.dismiss();
                }
            });
        });
    }

    @SuppressWarnings("unused")
    @NonNull
    @CheckResult
    public Observable<Void> observeCanceled() {
        return Observable.unsafeCreate(subscriber -> {
            onCancelDelegate = dialog1 -> {
                if (!subscriber.isUnsubscribed())
                    subscriber.onNext(null);
            };
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    onCancelDelegate = null;
                }
            });
        });
    }


    @NonNull
    @CheckResult
    public Observable<Void> observeDismiss() {
        return Observable.unsafeCreate(subscriber -> {
            onDismissListenerDelegate = dialog1 -> {
                if (!subscriber.isUnsubscribed())
                    subscriber.onNext(null);
            };
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    onDismissListenerDelegate = null;
                }
            });
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
        @StringRes
        int neutralButtonName = -1;

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

        @NonNull
        public RxAlertDialog.Builder negativeButton(@StringRes int name) {
            this.negativeButtonName = name;
            return this;
        }

        @NonNull
        public RxAlertDialog.Builder neutralButton(@StringRes int name) {
            this.neutralButtonName = name;
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
            if (neutralButtonName != -1) {
                builder.setNeutralButton(neutralButtonName, rxAlertDialog.clickListener);
            }
            builder.setOnCancelListener(rxAlertDialog.onCancel);
            rxAlertDialog.dialog = builder.create();
            rxAlertDialog.dialog.setOnDismissListener(rxAlertDialog.onDismissListener);
            rxAlertDialog.dialog.show();
            return rxAlertDialog;
        }

    }
}
