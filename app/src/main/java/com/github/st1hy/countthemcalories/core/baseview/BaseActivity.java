package com.github.st1hy.countthemcalories.core.baseview;

import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;

import rx.Observable;

public abstract class BaseActivity extends AppCompatActivity implements DialogView {

    protected final ApplicationComponent getAppComponent() {
        return ((CaloriesCounterApplication) getApplication()).getComponent();
    }

    @NonNull
    public static <T> T assertNotNull(@Nullable T t) {
        if (t == null) throw new NullPointerException();
        return t;
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, @ArrayRes int options) {
        return RxAlertDialog.Builder.with(this)
                .title(titleRes)
                .items(options)
                .show()
                .observeItemClick();
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, CharSequence[] options) {
        return RxAlertDialog.Builder.with(this)
                .title(titleRes)
                .items(options)
                .show()
                .observeItemClick();
    }
}
