package com.github.st1hy.countthemcalories.inject.activities.ingredients;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsScreenImpl;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.fragment.IngredientsFragmentComponentFactory;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class IngredientActivityBindings {

    @Binds
    public abstract IngredientsScreen provideView(IngredientsScreenImpl ingredientsScreen);

    @Binds
    public abstract Activity activity(AppCompatActivity activity);

    @Binds
    @Named("activityContext")
    public abstract Context context(Activity activity);

    @Binds
    public abstract IngredientsFragmentComponentFactory childComponentFactory(
            IngredientsActivityComponent component);

}
