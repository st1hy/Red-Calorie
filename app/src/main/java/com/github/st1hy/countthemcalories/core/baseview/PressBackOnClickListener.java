package com.github.st1hy.countthemcalories.core.baseview;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import javax.inject.Inject;


public class PressBackOnClickListener implements View.OnClickListener {

    @NonNull
    private final Activity activity;

    @Inject
    public PressBackOnClickListener(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        activity.onBackPressed();
    }
}
