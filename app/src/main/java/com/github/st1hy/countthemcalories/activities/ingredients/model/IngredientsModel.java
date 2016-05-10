package com.github.st1hy.countthemcalories.activities.ingredients.model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.addmeal.model.UnitNamesModel;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import javax.inject.Inject;

public class IngredientsModel {
    private final Intent intent;
    final UnitNamesModel unitNamesModel;

    @Inject
    public IngredientsModel(@Nullable Intent intent,
                            @NonNull UnitNamesModel unitNamesModel) {
        this.intent = intent;
        this.unitNamesModel = unitNamesModel;
    }

    public boolean isInSelectMode() {
        return intent != null && IngredientsActivity.ACTION_SELECT_INGREDIENT.equals(intent.getAction());
    }

    @NonNull
    public String getReadableEnergyDensity(@NonNull IngredientTemplate ingredient) {
        return unitNamesModel.getReadableEnergyDensity(ingredient.getEnergyDensity());
    }
}
