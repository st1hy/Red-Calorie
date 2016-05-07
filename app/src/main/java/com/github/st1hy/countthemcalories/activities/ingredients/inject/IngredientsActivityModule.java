package com.github.st1hy.countthemcalories.activities.ingredients.inject;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientsActivityModel;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
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
    public Activity provideActivity() {
        return activity;
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
                                                     IngredientsActivityModel activityModel,
                                                     IngredientTypesModel model,
                                                     SettingsModel settingsModel,
                                                     Picasso picasso) {
        return new IngredientsDaoAdapter(view, activityModel,model,settingsModel,picasso);
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
}
