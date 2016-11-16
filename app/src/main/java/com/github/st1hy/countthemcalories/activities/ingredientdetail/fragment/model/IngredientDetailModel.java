package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.google.common.base.Optional;

import java.math.BigDecimal;

import javax.inject.Inject;

@PerFragment
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
            if (EnergyDensityUtils.getOrZero(ingredientAmount).compareTo(BigDecimal.ZERO) <= 0) {
                return Optional.of(IngredientDetailError.ZERO_VALUE);
            }
        }
        return Optional.absent();
    }


}
