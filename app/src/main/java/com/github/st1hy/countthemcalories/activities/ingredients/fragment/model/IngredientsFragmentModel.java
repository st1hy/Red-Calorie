package com.github.st1hy.countthemcalories.activities.ingredients.fragment.model;

import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsFragment;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;

import javax.inject.Inject;


@PerFragment
public class IngredientsFragmentModel {
    private final Bundle arguments;
    final PhysicalQuantitiesModel physicalQuantitiesModel;

    @Inject
    public IngredientsFragmentModel(@NonNull Bundle arguments,
                                    @NonNull PhysicalQuantitiesModel physicalQuantitiesModel) {
        this.arguments = arguments;
        this.physicalQuantitiesModel = physicalQuantitiesModel;
    }

    public boolean isInSelectMode() {
        return arguments.getBoolean(IngredientsFragment.ARG_SELECT_BOOL, false);
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

    @ArrayRes
    public int getIngredientOptions() {
        return R.array.ingredient_item_options;
    }

    @StringRes
    public int getIngredientOptionsTitle() {
        return R.string.ingredients_item_options_title;
    }
}
