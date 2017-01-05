package com.github.st1hy.countthemcalories.core.activityresult;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.Observable;

public class ActivityLauncherImp implements ActivityLauncher {

    private final RxActivityResult rxActivityResult;
    private final ActivityLauncherSubject activityLauncherSubject;

    @Inject
    public ActivityLauncherImp(@NonNull RxActivityResult rxActivityResult,
                               @NonNull ActivityLauncherSubject activityLauncherSubject) {
        this.rxActivityResult = rxActivityResult;
        this.activityLauncherSubject = activityLauncherSubject;
    }

    private Observable<ActivityResult> startActivityForResult(@NonNull StartParams startParams) {
        int requestCode = startParams.getRequestCode();
        if (requestCode < 0)
            throw new IllegalArgumentException("requestCode must be greater than 0");
        Observable<ActivityResult> observable = rxActivityResult.prepareSubject(requestCode);
        Intent intent = startParams.getIntent();
        Bundle options = startParams.getOptions();
        try {
            activityLauncherSubject.startActivityForResult(intent, requestCode, options);
        } catch (ActivityNotFoundException e) {
            rxActivityResult.onError(requestCode, e);
        }
        return observable;
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
