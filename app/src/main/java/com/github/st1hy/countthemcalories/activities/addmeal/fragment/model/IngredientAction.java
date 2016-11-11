package com.github.st1hy.countthemcalories.activities.addmeal.fragment.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.database.Ingredient;

public class IngredientAction {
    public static final IngredientAction CANCELED = new IngredientAction(Type.CANCELED, -1L, null);
    @NonNull
    private final Type type;
    private final long id;
    private final Ingredient ingredient;

    private IngredientAction(@NonNull Type type, long id,
                             Ingredient ingredient) {
        this.type = type;
        this.id = id;
        this.ingredient = ingredient;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public Ingredient getIngredient() {
        return ingredient;
    }

    @NonNull
    public static IngredientAction valueOf(@NonNull Type type, long id,
                                           @Nullable Ingredient ingredient) {
        if (type == Type.CANCELED) return CANCELED;
        return new IngredientAction(type, id, ingredient);
    }

    public enum Type {
        EDIT, REMOVE, CANCELED
    }
}
