package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment.model;

import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;

public enum IngredientDetailError {
    NO_VALUE(R.string.add_meal_amount_error_empty),
    ZERO_VALUE(R.string.add_meal_amount_error_zero);

    private final int errorResId;

    IngredientDetailError(@StringRes int errorResId) {
        this.errorResId = errorResId;
    }

    @StringRes
    public int getErrorResId() {
        return errorResId;
    }

}
