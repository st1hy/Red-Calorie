package com.github.st1hy.countthemcalories.activities.ingredients.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;

public class AddIngredientParams {
    @NonNull
    private final AddIngredientType type;
    @NonNull
    private final String extraName;

    private AddIngredientParams(@NonNull AddIngredientType type, @NonNull String extraName) {
        this.type = type;
        this.extraName = extraName;
    }

    @NonNull
    public static AddIngredientParams of(@NonNull AddIngredientType type, @NonNull String extraName) {
        return new AddIngredientParams(type, extraName);
    }

    @NonNull
    public AddIngredientType getType() {
        return type;
    }

    @NonNull
    public String getExtraName() {
        return extraName;
    }
}
