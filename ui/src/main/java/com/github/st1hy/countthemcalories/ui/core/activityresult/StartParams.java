package com.github.st1hy.countthemcalories.ui.core.activityresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class StartParams {
    @NonNull
    private final Intent intent;
    private final int requestCode;
    @Nullable
    private final Bundle options;

    private StartParams(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        this.intent = intent;
        this.requestCode = requestCode;
        this.options = options;
    }

    /**
     * @param intent      The intent to start.
     * @param requestCode If >= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     * @param options     Additional options for how the Activity should be started.
     *                    See {@link android.content.Context#startActivity(Intent, Bundle)
     */
    @NonNull
    public static StartParams of(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        return new StartParams(intent, requestCode, options);
    }


    @NonNull
    public static StartParams of(@NonNull Intent intent, int requestCode) {
        return new StartParams(intent, requestCode, null);
    }

    @NonNull
    public Intent getIntent() {
        return intent;
    }

    public int getRequestCode() {
        return requestCode;
    }

    @Nullable
    public Bundle getOptions() {
        return options;
    }
}
