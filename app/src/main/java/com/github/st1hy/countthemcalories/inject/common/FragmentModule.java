package com.github.st1hy.countthemcalories.inject.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.quantifier.bundle.FragmentSavedState;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

    @NonNull
    private final AddIngredientFragment fragment;
    @Nullable
    private final Bundle savedState;

    public FragmentModule(@NonNull AddIngredientFragment fragment, @Nullable Bundle savedState) {
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
    @PerFragment
    @FragmentRootView
    public View rootView() {
        return fragment.getView();
    }


}
