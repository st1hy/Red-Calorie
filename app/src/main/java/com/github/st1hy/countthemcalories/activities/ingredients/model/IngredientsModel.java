package com.github.st1hy.countthemcalories.activities.ingredients.model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;

import javax.inject.Inject;

public class IngredientsModel {
    private final Intent intent;
    final PhysicalQuantitiesModel physicalQuantitiesModel;

    @Inject
    public IngredientsModel(@Nullable Intent intent,
                            @NonNull PhysicalQuantitiesModel physicalQuantitiesModel) {
        this.intent = intent;
        this.physicalQuantitiesModel = physicalQuantitiesModel;
    }

    public boolean isInSelectMode() {
        return intent != null && IngredientsActivity.ACTION_SELECT_INGREDIENT.equals(intent.getAction());
    }

    @NonNull
    public String getReadableEnergyDensity(@NonNull EnergyDensity energyDensity) {
        return physicalQuantitiesModel.convertAndFormat(energyDensity);
    }

    @StringRes
    public int getUndoDeleteMessage() {
        return R.string.ingredients_undo_remove;
    }

    @StringRes
    public int getNoIngredientsMessage() {
        return R.string.ingredients_no_ingredients_yet;
    }

    @StringRes
    public int getSearchEmptyMessage() {
        return R.string.ingredients_search_result_empty;
    }
}
