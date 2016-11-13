package com.github.st1hy.countthemcalories.activities.ingredients.fragment.model;

import android.support.annotation.NonNull;

public enum IngredientOptions {

    ADD_TO_NEW, EDIT, REMOVE, UNKNOWN;

    @NonNull
    public static IngredientOptions from(int selectedItemPosition) {
        switch (selectedItemPosition) {
            case 0:
                return ADD_TO_NEW;
            case 1:
                return EDIT;
            case 2:
                return REMOVE;
            default:
                return UNKNOWN;
        }
    }
}
