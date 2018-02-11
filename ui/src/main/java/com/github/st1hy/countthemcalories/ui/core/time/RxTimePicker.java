package com.github.st1hy.countthemcalories.ui.core.time;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.android.MainThreadSubscription;
import rx.functions.Func1;

public class RxTimePicker {

    @NonNull
    @CheckResult
    public static Observable<DateTime> openPicker(Context context, DateTime startingTime) {
        boolean is24h = DateFormat.is24HourFormat(context);
        int hour = startingTime.getHourOfDay();
        int minute = startingTime.getMinuteOfHour();
        final AtomicReference<Func1<DateTime, Void>> output = new AtomicReference<>(null);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view, hourOfDay, minute1) -> {
                    MutableDateTime outputTime = startingTime.toMutableDateTime();
                    outputTime.setMinuteOfDay(minute1);
                    outputTime.setHourOfDay(hourOfDay);
                    Func1<DateTime, Void> dateTimeCallable = output.get();
                    if (dateTimeCallable != null) {
                        dateTimeCallable.call(outputTime.toDateTime());
                    }
                },
                hour, minute, is24h);
        timePickerDialog.show();
        return Observable.unsafeCreate(subscriber -> {
            output.set(dateTime -> {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(dateTime);
                    subscriber.onCompleted();
                }
                return null;
            });
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    timePickerDialog.cancel();
                }
            });
        });
    }
}
