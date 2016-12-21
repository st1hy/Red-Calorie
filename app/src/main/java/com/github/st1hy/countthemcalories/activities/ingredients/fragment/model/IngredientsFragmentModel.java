package com.github.st1hy.countthemcalories.activities.ingredients.fragment.model;

import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;
import javax.inject.Named;


@PerFragment
public class IngredientsFragmentModel {
    private final boolean isInSelectMode;
    @NonNull
    private final PhysicalQuantitiesModel physicalQuantitiesModel;

    @Inject
    public IngredientsFragmentModel(@Named("isInSelectMode") boolean isInSelectMode,
                                    @NonNull PhysicalQuantitiesModel physicalQuantitiesModel) {
        this.isInSelectMode = isInSelectMode;
        this.physicalQuantitiesModel = physicalQuantitiesModel;
    }

    public boolean isInSelectMode() {
        return isInSelectMode;
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
