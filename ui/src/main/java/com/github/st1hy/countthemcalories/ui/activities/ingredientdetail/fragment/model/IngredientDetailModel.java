package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.ui.activities.settings.model.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.google.common.base.Optional;

import javax.inject.Inject;

@PerFragment
@SuppressWarnings("Guava")
public class IngredientDetailModel {

    public static final String SAVED_INGREDIENT_MODEL = "saved ingredient model";
    private final Resources resources;

    @Inject
    public IngredientDetailModel(@NonNull Resources resources) {
        this.resources = resources;
    }

    @Nullable
    public String getErrorString(@NonNull String ingredientAmount) {
        Optional<IngredientDetailError> optionalError = getOptionalError(ingredientAmount);
        if (optionalError.isPresent()) {
            return resources.getString(optionalError.get().getErrorResId());
        } else {
            return null;
        }
    }

    @NonNull
    private Optional<IngredientDetailError> getOptionalError(@NonNull String ingredientAmount) {
        if (ingredientAmount.trim().isEmpty()) {
            return Optional.of(IngredientDetailError.NO_VALUE);
        } else {
            if (EnergyDensityUtils.getOrZero(ingredientAmount) <= 0) {
                return Optional.of(IngredientDetailError.ZERO_VALUE);
            }
        }
        return Optional.absent();
    }


}
