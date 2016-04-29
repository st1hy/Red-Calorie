package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.addingredient.presenter.AddIngredientPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.presenter.AddIngredientPresenterImp;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientView;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.permissions.PermissionSubject;

import dagger.Module;
import dagger.Provides;

@Module
public class AddIngredientModule {
    private final AddIngredientActivity activity;
    private final Bundle bundle;

    public AddIngredientModule(@NonNull AddIngredientActivity activity, @Nullable Bundle savedState) {
        this.activity = activity;
        this.bundle = savedState;
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return activity;
    }

    @Provides
    @PerActivity
    public AddIngredientView provideView() {
        return activity;
    }

    @Provides
    @PerActivity
    public AddIngredientPresenter providePresenter(AddIngredientPresenterImp presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public PermissionSubject providePermissionSubject() {
        return activity;
    }

    @Provides
    @PerActivity
    @Nullable
    public Bundle provideSavedStateBundle() {
        return bundle;
    }
}
