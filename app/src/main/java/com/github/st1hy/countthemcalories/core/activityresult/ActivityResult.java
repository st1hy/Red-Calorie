package com.github.st1hy.countthemcalories.core.activityresult;

import android.app.Activity;
import android.content.Intent;

import rx.functions.Func1;

/**
 * This class holds the data received by {@link Activity#onActivityResult(int, int, Intent)}.
 */
public class ActivityResult {

    public static final Func1<ActivityResult, Boolean> IS_OK = new Func1<ActivityResult, Boolean>() {
        @Override
        public Boolean call(ActivityResult activityResult) {
            return activityResult.isOk();
        }
    };
    private final int resultCode;
    private final int requestCode;
    private final Intent data;

    public ActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public Intent getData() {
        return data;
    }

    public boolean isOk() {
        return resultCode == Activity.RESULT_OK;
    }

    public boolean isCanceled() {
        return resultCode == Activity.RESULT_CANCELED;
    }

}
