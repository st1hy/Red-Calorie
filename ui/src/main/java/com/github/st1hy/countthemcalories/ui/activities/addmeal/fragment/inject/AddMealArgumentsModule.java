package com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.inject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.CopyMealActivity;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.model.EditMealMode;
import com.github.st1hy.countthemcalories.ui.contract.Ingredient;
import com.github.st1hy.countthemcalories.ui.contract.IngredientFactory;
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate;
import com.github.st1hy.countthemcalories.ui.contract.Meal;
import com.github.st1hy.countthemcalories.ui.contract.MealFactory;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.bundle.FragmentSavedState;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.datetime.NewMealDate;

import org.joda.time.DateTime;
import org.parceler.Parcels;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class AddMealArgumentsModule {

    @Provides
    @PerFragment
    public static Meal provideMeal(@Nullable @FragmentSavedState Bundle savedState,
                                   Intent intent, EditMealMode editMealMode, MealFactory factory) {
        if (savedState != null) {
            return Parcels.unwrap(savedState.getParcelable(AddMealModel.SAVED_MEAL_STATE));
        } else {
            Meal editedMeal = Parcels.unwrap(intent.getParcelableExtra(AddMealActivity.EXTRA_MEAL_PARCEL));
            if (editedMeal != null) {
                if (editMealMode == EditMealMode.COPY_TO_TODAY) {
                    editedMeal = factory.newMeal(editedMeal);
                }
                return editedMeal;
            } else {
                editedMeal = factory.newMeal();
                editedMeal.setName("");
                editedMeal.setImageUri(Uri.EMPTY);
                return editedMeal;
            }
        }
    }

    @Provides
    public static EditMealMode editMealMode(Activity activity) {
        if (activity instanceof CopyMealActivity) {
            return EditMealMode.COPY_TO_TODAY;
        } else {
            return EditMealMode.EDIT;
        }
    }

    @Provides
    @Nullable
    public static IngredientTemplate provideExtraIngredientTemplate(Intent intent) {
        IngredientTemplate ingredientTemplate = Parcels.unwrap(
                intent.getParcelableExtra(AddMealActivity.EXTRA_INGREDIENT_PARCEL)
        );
        intent.removeExtra(AddMealActivity.EXTRA_INGREDIENT_PARCEL);
        return ingredientTemplate;
    }

    @Provides
    @Nullable
    public static Ingredient extraIngredient(@Nullable IngredientTemplate extraTemplate,
                                             @NonNull IngredientFactory factory) {
        if (extraTemplate != null) {
            return factory.newIngredient();
        } else {
            return null;
        }
    }

    @Provides
    @Nullable
    @NewMealDate
    public static DateTime newMealDate(Intent intent) {
        return (DateTime) intent.getSerializableExtra(AddMealActivity.EXTRA_NEW_MEAL_DATE);
    }
}
