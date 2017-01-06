package com.github.st1hy.countthemcalories.inject.activities.ingredients.fragment;

import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsViewController;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class IngredientFragmentBindings {

    @Binds
    public abstract IngredientsView provideView(IngredientsViewController controller);

    @Binds
    public abstract IngredientsPresenter ingredientsPresenter(IngredientsPresenterImpl presenter);

}
