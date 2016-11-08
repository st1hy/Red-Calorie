package com.github.st1hy.countthemcalories.core.rx.activityresult;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Provide a way to receive the results of the {@link Activity} with RxJava.
 */
public class RxActivityResult {

    static final String INTENT = "intent";
    static final String REQUEST_CODE = "request";
    static final String BUNDLE = "Bundle";

    private final String packageName;
    private Map<Integer, PublishSubject<ActivityResult>> results = new HashMap<>();

    public RxActivityResult(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Create new {@link Launchable} from launch source component({@link Activity}) of other activity.
     *
     * @param context requesting result
     * @return New {@link Launchable} instance.
     */
    @CheckResult
    public Launchable from(@NonNull final Context context) {
        return new Launcher(this, context);
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        PublishSubject<ActivityResult> subject = results.remove(requestCode);
        ActivityResult result = new ActivityResult(requestCode, resultCode, data);
        subject.onNext(result);
        subject.onCompleted();
    }

    void onError(int requestCode, ActivityNotFoundException exception) {
        PublishSubject<ActivityResult> subject = results.remove(requestCode);
        subject.onError(exception);
    }

    private Intent prepareIntent(final Intent intent, final int requestCode, @Nullable final Bundle options) {
        Intent shadowIntent = new Intent();
        shadowIntent.setComponent(new ComponentName(packageName, IntentHandlerActivity.class.getName()));
        shadowIntent.putExtra(INTENT, intent);
        shadowIntent.putExtra(REQUEST_CODE, requestCode);
        if (options != null)
            shadowIntent.putExtra(BUNDLE, options);
        return shadowIntent;
    }

    private PublishSubject<ActivityResult> prepareSubject(final int requestCode) {
        PublishSubject<ActivityResult> subject = results.get(requestCode);
        if (subject == null)
            subject = PublishSubject.create();
        results.put(requestCode, subject);
        return subject;
    }

    private static class Launcher implements Launchable {

        private final RxActivityResult rxActivityResult;
        private final Context context;

        Launcher(RxActivityResult rxActivityResult, Context context) {
            this.rxActivityResult = rxActivityResult;
            this.context = context;
        }

        void startActivity(Intent intent) {
            context.startActivity(intent);
        }

        @Override
        public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent, int requestCode) {
            return startActivityForResult(intent, requestCode, null);
        }

        @Override
        public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent, int requestCode,
                                                                 @Nullable Bundle options) {
            if (requestCode < 0)
                throw new IllegalArgumentException("requestCode must be greater than 0");

            PublishSubject<ActivityResult> subject = rxActivityResult.prepareSubject(requestCode);
            Intent shadowIntent = rxActivityResult.prepareIntent(intent, requestCode, options);
            startActivity(shadowIntent);
            return subject;
        }
    }

}
