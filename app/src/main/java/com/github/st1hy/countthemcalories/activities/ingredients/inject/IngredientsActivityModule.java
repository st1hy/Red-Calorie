package com.github.st1hy.countthemcalories.activities.ingredients.inject;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientsModel;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module
public class IngredientsActivityModule {
    private final IngredientsActivity activity;

    public IngredientsActivityModule(@NonNull IngredientsActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public IngredientsView provideView() {
        return activity;
    }

    @Provides
    @PerActivity
    public Intent provideIntent() {
        return activity.getIntent();
    }

    @PerActivity
    @Provides
    public IngredientsDaoAdapter providePresenterImp(IngredientsView view,
                                                     IngredientsModel model,
                                                     IngredientTypesDatabaseModel databaseModel,
                                                     Picasso picasso) {
        return new IngredientsDaoAdapter(view, model, databaseModel, picasso);
    }

    @Provides
    @PerActivity
    public IngredientsPresenter providePresenter(IngredientsPresenterImpl presenter) {
        return presenter;
    }

    @PerActivity
    @Provides
    public DrawerPresenter provideDrawerPresenter(IngredientsPresenter presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public Resources provideResources() {
        return activity.getResources();
    }
}
