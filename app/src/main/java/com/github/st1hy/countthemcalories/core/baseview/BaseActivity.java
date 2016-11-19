package com.github.st1hy.countthemcalories.core.baseview;

import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.inject.application.ApplicationComponent;

public abstract class BaseActivity extends AppCompatActivity {

    protected final ApplicationComponent getAppComponent() {
        return CaloriesCounterApplication.get(this).getComponent();
    }

}
