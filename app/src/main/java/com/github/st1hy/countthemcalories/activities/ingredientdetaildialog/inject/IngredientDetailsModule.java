package com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.inject;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.presenter.IngredientDetailPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view.IngredientDetailView;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view.IngredientDetailsActivity;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class IngredientDetailsModule {
    private final IngredientDetailsActivity activity;
    private final Bundle bundle;

    public IngredientDetailsModule(@NonNull IngredientDetailsActivity activity, @Nullable Bundle bundle) {
        this.activity = activity;
        this.bundle = bundle;
    }

    @Provides
    @PerActivity
    @Nullable
    public Bundle provideBundle() {
        return bundle;
    }

    @Provides
    @PerActivity
    public IngredientDetailView provideView() {
        return activity;
    }

    @Provides
    @PerActivity
    public IngredientDetailPresenter providePresenter(IngredientDetailPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public Intent provideIntent() {
        return activity.getIntent();
    }

    @Provides
    @PerActivity
    public Resources provideResources() {
        return activity.getResources();
    }
}
