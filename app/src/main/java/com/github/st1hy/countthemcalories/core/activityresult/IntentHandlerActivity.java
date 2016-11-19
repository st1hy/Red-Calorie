package com.github.st1hy.countthemcalories.core.activityresult;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;

import javax.inject.Inject;

/**
 * Wrapper for onActivityResult
 */
public class IntentHandlerActivity extends Activity {

    @Inject
    RxActivityResult rxActivityResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CaloriesCounterApplication.get(this).getComponent()
                .newIntentHandlerActivityComponent()
                .inject(this);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Intent requestIntent = intent.getParcelableExtra(RxActivityResult.INTENT);
        int requestCode = intent.getIntExtra(RxActivityResult.REQUEST_CODE, 0);

        try {
            startActivityForResult(requestIntent, requestCode);
        } catch (ActivityNotFoundException exception) {
            rxActivityResult.onError(requestCode, exception);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        rxActivityResult.onActivityResult(requestCode, resultCode, data);
        finish();
    }

}
