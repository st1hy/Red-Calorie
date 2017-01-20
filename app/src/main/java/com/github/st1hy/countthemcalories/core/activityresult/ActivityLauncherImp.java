package com.github.st1hy.countthemcalories.core.activityresult;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.Observable;

public class ActivityLauncherImp implements ActivityLauncher {

    private final ActivityLauncherSubject activityLauncherSubject;

    @Inject
    public ActivityLauncherImp(@NonNull ActivityLauncherSubject activityLauncherSubject) {
        this.activityLauncherSubject = activityLauncherSubject;
    }

    @NonNull
    @Override
    public Observable.Transformer<StartParams, ActivityResult> startActivityForResult(
            final int requestCode) {
        if (requestCode < 0)
            throw new IllegalArgumentException("requestCode must be greater than 0");
        return startParamsObservable -> startParamsObservable
                .flatMap(activityLauncherSubject::startActivityForResult)
                .mergeWith(activityLauncherSubject.attachToExistingRequest(requestCode))
                .distinctUntilChanged();
    }

}
