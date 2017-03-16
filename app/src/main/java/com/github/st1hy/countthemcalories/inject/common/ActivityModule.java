package com.github.st1hy.countthemcalories.inject.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.core.inject.InputMethodManagerModule;
import com.github.st1hy.countthemcalories.inject.quantifier.bundle.ActivitySavedState;

import dagger.Module;
import dagger.Provides;

@Module(includes = {
        ActivityBindings.class,
        InputMethodManagerModule.class
})
public class ActivityModule {

    @NonNull
    private final AppCompatActivity activity;
    @Nullable
    private final Bundle savedInstanceState;

    public ActivityModule(@NonNull AppCompatActivity activity) {
        this(activity, null);
    }

    public ActivityModule(@NonNull AppCompatActivity activity, @Nullable Bundle savedInstanceState) {
        this.activity = activity;
        this.savedInstanceState = savedInstanceState;
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
    }

    @Provides
    @ActivitySavedState
    @Nullable
    public Bundle savedState() {
        return savedInstanceState;
    }
}
