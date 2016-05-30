package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.addingredient.model.IngredientTagsModel;
import com.github.st1hy.countthemcalories.activities.addingredient.presenter.AddIngredientPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.presenter.AddIngredientPresenterImp;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientView;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.permissions.PermissionSubject;
import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenter;

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

    @Provides
    @PerActivity
    public WithPicturePresenter providePicturePresenter(AddIngredientPresenter presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public IngredientTagsModel provideIngredientTagModel(@Nullable Bundle savedState) {
        return new IngredientTagsModel(savedState);
    }

    @Provides
    @PerActivity
    public Resources provideResources() {
        return activity.getResources();
    }

    @Provides
    @PerActivity
    public Intent provideIntent() {
        return activity.getIntent();
    }
}
