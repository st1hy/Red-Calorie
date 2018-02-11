package com.github.st1hy.countthemcalories.ui.inject.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.bundle.FragmentArguments;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.bundle.FragmentSavedState;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

    @NonNull
    private final Fragment fragment;
    @Nullable
    private final Bundle savedState;

    public FragmentModule(@NonNull Fragment fragment, @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @Nullable
    @FragmentSavedState
    public Bundle provideSavedStateBundle() {
        return savedState;
    }

    @Provides
    @FragmentArguments
    public Bundle arguments() {
        return fragment.getArguments();
    }

    @Provides
    @PerFragment
    @FragmentRootView
    public View rootView() {
        return fragment.getView();
    }


}
