package com.github.st1hy.countthemcalories.core.activityresult;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import rx.Observable;

public interface ActivityLauncherSubject {

    @CheckResult
    Observable<ActivityResult> startActivityForResult(@NonNull StartParams startParams);

    @CheckResult
    Observable<? extends ActivityResult> attachToExistingRequest(int requestCode);
}
