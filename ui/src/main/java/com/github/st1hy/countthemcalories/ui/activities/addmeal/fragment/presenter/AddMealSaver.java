package com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.ui.core.WithState;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.database.Meal;

import org.parceler.Parcels;

import javax.inject.Inject;

public class AddMealSaver implements WithState {

    @NonNull
    private final Meal meal;
    @NonNull
    private final MealIngredientsListModel ingredientsListModel;
    @NonNull
    private final PicturePicker picturePicker;

    @Inject
    public AddMealSaver(@NonNull Meal meal,
                        @NonNull MealIngredientsListModel ingredientsListModel,
                        @NonNull PicturePicker picturePicker) {
        this.meal = meal;
        this.ingredientsListModel = ingredientsListModel;
        this.picturePicker = picturePicker;
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(AddMealModel.SAVED_MEAL_STATE, Parcels.wrap(meal));
        outState.putParcelable(MealIngredientsListModel.SAVED_INGREDIENTS,
                Parcels.wrap(ingredientsListModel));
        picturePicker.onSaveState(outState);
    }
}
