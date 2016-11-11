package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.core.WithState;
import com.github.st1hy.countthemcalories.database.Meal;

import org.parceler.Parcels;

import javax.inject.Inject;

public class AddMealSaver implements WithState {

    @NonNull private final Meal meal;
    @NonNull private final MealIngredientsListModel ingredientsListModel;

    @Inject
    public AddMealSaver(@NonNull Meal meal, @NonNull MealIngredientsListModel ingredientsListModel) {
        this.meal = meal;
        this.ingredientsListModel = ingredientsListModel;
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(AddMealModel.SAVED_MEAL_STATE, Parcels.wrap(meal));
        outState.putParcelable(MealIngredientsListModel.SAVED_INGREDIENTS, Parcels.wrap(ingredientsListModel));
    }
}
