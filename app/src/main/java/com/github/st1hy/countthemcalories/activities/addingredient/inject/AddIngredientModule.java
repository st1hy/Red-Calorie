package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import android.app.Activity;
import android.support.annotation.NonNull;

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

    public AddIngredientModule(@NonNull AddIngredientActivity activity) {
        this.activity = activity;
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
}
