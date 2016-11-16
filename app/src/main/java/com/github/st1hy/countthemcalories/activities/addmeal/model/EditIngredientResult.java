package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;

public enum EditIngredientResult {
    EDIT, REMOVE, UNKNOWN;

    /**
     * @return result of ingredient detail activity or #UNKNOWN if not recognised
     */
    @NonNull
    public static EditIngredientResult fromIngredientDetailResult(int resultCode) {
        switch (resultCode) {
            case IngredientDetailActivity.RESULT_OK:
                return EDIT;
            case IngredientDetailActivity.RESULT_REMOVE:
                return REMOVE;
            default:
                return UNKNOWN;
        }
    }
}
