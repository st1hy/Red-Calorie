package com.github.st1hy.countthemcalories.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.inject.component.ApplicationComponent;

public class BaseActivity extends AppCompatActivity {


    protected final ApplicationComponent getAppComponent() {
        return ((CaloriesCounterApplication) getApplication()).getComponent();
    }

    @NonNull
    public static <T> T assertNotNull(@Nullable T t) {
        if (t == null) throw new NullPointerException();
        return t;
    }

}
