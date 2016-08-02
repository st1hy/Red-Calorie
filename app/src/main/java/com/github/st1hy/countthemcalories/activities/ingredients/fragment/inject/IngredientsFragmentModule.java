package com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientsFragmentModel;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsFragment;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.commands.IngredientsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.google.common.base.Preconditions;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module
public class IngredientsFragmentModule {

    private final IngredientsFragment fragment;

    public IngredientsFragmentModule(IngredientsFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public IngredientsScreen provideIngredientsScreen() {
        FragmentActivity activity = fragment.getActivity();
        Preconditions.checkState(activity instanceof IngredientsScreen, "activity must implement " +
                IngredientsScreen.class.getSimpleName());
        return (IngredientsScreen) activity;
    }


    @PerFragment
    @Provides
    public IngredientsDaoAdapter provideIngredientsAdapter(IngredientsView view,
                                                           IngredientsFragmentModel model,
                                                           RxIngredientsDatabaseModel databaseModel,
                                                           IngredientsDatabaseCommands commands,
                                                           Picasso picasso,
                                                           PermissionsHelper permissionsHelper) {
        return new IngredientsDaoAdapter(view, model, databaseModel, commands, picasso, permissionsHelper);
    }

    @Provides
    @PerFragment
    public IngredientsPresenter providePresenter(IngredientsView view) {
        return new IngredientsPresenterImpl(view);
    }

    @PerFragment
    @Provides
    public IngredientsFragmentModel provideModel(Bundle arguments,
                                                 PhysicalQuantitiesModel quantitiesModel) {
        return new IngredientsFragmentModel(arguments, quantitiesModel);
    }

    @Provides
    public Bundle provideArguments() {
        return fragment.getArguments();
    }

    @Provides
    public IngredientsView provideView() {
        return fragment;
    }

    @Provides
    public Resources provideResources() {
        return fragment.getResources();
    }

    @Provides
    public FragmentActivity provideFragmentActivity() {
        return fragment.getActivity();
    }
}
