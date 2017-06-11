package com.github.st1hy.countthemcalories.core.activityresult;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.core.rx.QueueSubject;

import rx.Observable;

public class IntentHandlerFragment extends BaseFragment implements ActivityLauncherSubject {
    public static final String TAG = "IntentHandlerFragment";

    private final SparseArray<QueueSubject<ActivityResult>> results = new SparseArray<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        QueueSubject<ActivityResult> subject = getSubject(requestCode);
        subject.onNext(new ActivityResult(requestCode, resultCode, data));
    }

    @Override
    @CheckResult
    public Observable<? extends ActivityResult> attachToExistingRequest(int requestCode) {
        return getSubject(requestCode);
    }

    @Override
    @CheckResult
    public Observable<ActivityResult> startActivityForResult(@NonNull StartParams startParams) {
        int requestCode = startParams.getRequestCode();
        if (requestCode < 0)
            throw new IllegalArgumentException("requestCode must be greater than 0");
        Observable<ActivityResult> observable = getSubject(requestCode);
        Intent intent = startParams.getIntent();
        Bundle options = startParams.getOptions();
        try {
            startActivityForResult(intent, requestCode, options);
        } catch (ActivityNotFoundException e) {
            onError(requestCode, e);
        }
        return observable;
    }

    @NonNull
    @CheckResult
    private QueueSubject<ActivityResult> getSubject(final int requestCode) {
        QueueSubject<ActivityResult> subject = results.get(requestCode);
        if (subject == null) {
            subject = QueueSubject.create();
            results.put(requestCode, subject);
        }
        return subject;
    }


    private void onError(int requestCode, @NonNull ActivityNotFoundException exception) {
        getSubject(requestCode).onError(exception);
    }

}
