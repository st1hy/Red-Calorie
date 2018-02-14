package com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.ui.R;

public enum IngredientTypeCreateError {
    NO_NAME(R.string.add_ingredient_name_error_empty, InputType.NAME),
    NO_VALUE(R.string.add_ingredient_energy_density_error_empty, InputType.VALUE),
    ZERO_VALUE(R.string.add_ingredient_energy_density_error_zero, InputType.VALUE);

    private final int errorResId;
    @NonNull
    private final InputType inputType;

    IngredientTypeCreateError(@StringRes int errorResId, @NonNull InputType inputType) {
        this.errorResId = errorResId;
        this.inputType = inputType;
    }

    @StringRes
    public int getErrorResId() {
        return errorResId;
    }

    @NonNull
    public InputType getInputType() {
        return inputType;
    }
}
