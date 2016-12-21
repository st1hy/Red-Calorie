package com.github.st1hy.countthemcalories.core.activityresult;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.github.st1hy.countthemcalories.core.rx.QueueSubject;

import rx.Observable;

/**
 * Provide a way to receive the results of the {@link Activity} with RxJava.
 */
public class RxActivityResult {

    static final String INTENT = "intent";
    static final String REQUEST_CODE = "request";
    private static final String BUNDLE = "Bundle";

    private final String packageName;
    private final SparseArray<QueueSubject<ActivityResult>> results = new SparseArray<>();

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
        QueueSubject<ActivityResult> subject = results.get(requestCode);
        subject.onNext(new ActivityResult(requestCode, resultCode, data));
        subject.onCompleted();
    }

    void onError(int requestCode, ActivityNotFoundException exception) {
        results.get(requestCode).onError(exception);
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

    private Observable<ActivityResult> prepareSubject(final int requestCode) {
        QueueSubject<ActivityResult> subject = results.get(requestCode);
        if (subject == null) {
            subject = QueueSubject.create();
            subject.doAfterTerminate(() -> results.remove(requestCode));
        }
        results.put(requestCode, subject);
        return subject;
    }

    private Observable<? extends ActivityResult> attachToExistingRequest(int requestCode) {
        Observable<ActivityResult> resultSubject = results.get(requestCode);
        if (resultSubject == null) resultSubject = Observable.empty();
        return resultSubject;
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

        private Observable<ActivityResult> startActivityForResult(@NonNull StartParams startParams) {
            int requestCode = startParams.getRequestCode();
            if (requestCode < 0)
                throw new IllegalArgumentException("requestCode must be greater than 0");
            Intent shadowIntent = rxActivityResult.prepareIntent(startParams.getIntent(),
                    requestCode, startParams.getOptions());
            startActivity(shadowIntent);
            return rxActivityResult.prepareSubject(requestCode);
        }


        @NonNull
        @Override
        public Observable.Transformer<StartParams, ActivityResult> startActivityForResult(
                final int requestCode) {
            if (requestCode < 0)
                throw new IllegalArgumentException("requestCode must be greater than 0");
            return startParamsObservable -> startParamsObservable
                    .flatMap(this::startActivityForResult)
                    .mergeWith(rxActivityResult.attachToExistingRequest(requestCode))
                    .distinctUntilChanged();
        }

    }

}
