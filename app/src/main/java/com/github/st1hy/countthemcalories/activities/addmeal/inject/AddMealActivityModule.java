package com.github.st1hy.countthemcalories.activities.addmeal.inject;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.addmeal.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenterImp;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.IngredientsAdapter;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.permissions.PermissionSubject;
import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenter;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module
public class AddMealActivityModule {
    private final AddMealActivity activity;
    private final Bundle bundle;

    public AddMealActivityModule(@NonNull AddMealActivity activity, @Nullable Bundle bundle) {
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
    public AddMealView provideView() {
        return activity;
    }

    @Provides
    @PerActivity
    public PermissionSubject providePermissionSubject() {
        return activity;
    }

    @Provides
    @PerActivity
    public AddMealPresenter providePresenter(AddMealPresenterImp presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public WithPicturePresenter providePicturePresenter(AddMealPresenterImp presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public MealIngredientsListModel provideListModel(IngredientTypesDatabaseModel model,
                                                     @Nullable Bundle savedState) {
        return new MealIngredientsListModel(model, savedState);
    }

    @Provides
    @PerActivity
    public IngredientsAdapter provideListAdapter(AddMealView view, MealIngredientsListModel model,
                                                 PhysicalQuantitiesModel namesModel, Picasso picasso) {
        return new IngredientsAdapter(view, model, namesModel, picasso);
    }

    @Provides
    @PerActivity
    public Resources provideResources() {
        return activity.getResources();
    }
}
