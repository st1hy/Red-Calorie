package com.github.st1hy.countthemcalories.inject.activities.addingredient;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreen;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreenImpl;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.fragment.AddIngredientFragmentComponentFactory;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AddIngredientBindings {

    @Binds
    public abstract Activity activity(AppCompatActivity activity);

    @Binds
    @Named("activityContext")
    public abstract Context activityContext(AppCompatActivity activity);

    @Binds
    public abstract AddIngredientScreen addIngredientScreen(AddIngredientScreenImpl screen);

    @Binds
    public abstract AddIngredientFragmentComponentFactory fragmentComponentFactory(
            AddIngredientComponent component);

}
