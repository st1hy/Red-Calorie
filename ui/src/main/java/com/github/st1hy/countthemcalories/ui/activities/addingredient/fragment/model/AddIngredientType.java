package com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model;

import android.support.annotation.NonNull;

public enum AddIngredientType {

    DRINK("add drink ingredient"), MEAL("add meal ingredient");

    final String action;

    AddIngredientType(String action) {
        this.action = action;
    }

    @NonNull
    public String getAction() {
        return action;
    }
}
