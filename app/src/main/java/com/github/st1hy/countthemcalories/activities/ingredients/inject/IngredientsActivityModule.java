package com.github.st1hy.countthemcalories.activities.ingredients.inject;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientsModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.commands.IngredientsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.tokensearch.model.SearchBarModel;
import com.github.st1hy.countthemcalories.core.tokensearch.presenter.SearchBarPresenter;
import com.github.st1hy.countthemcalories.core.tokensearch.view.SearchBarHolder;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module
public class IngredientsActivityModule {
    private final IngredientsActivity activity;
    private final Bundle savedState;

    public IngredientsActivityModule(@NonNull IngredientsActivity activity, @Nullable Bundle savedState) {
        this.activity = activity;
        this.savedState = savedState;
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
                                                     RxIngredientsDatabaseModel databaseModel,
                                                     IngredientsDatabaseCommands commands,
                                                     Picasso picasso) {
        return new IngredientsDaoAdapter(view, model, databaseModel, commands, picasso);
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

    @Provides
    @PerActivity
    public SearchBarPresenter provideSearchBar(SearchBarHolder viewHolder, SearchBarModel model) {
        return new SearchBarPresenter(viewHolder, model);
    }

    @Provides
    @PerActivity
    public SearchBarHolder provideSearchViewHolder() {
        return new SearchBarHolder(activity);
    }

    @Provides
    @PerActivity
    public SearchBarModel provideSearchBarModel() {
        return new SearchBarModel(savedState);
    }

}
