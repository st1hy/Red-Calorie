package com.github.st1hy.countthemcalories.activities.addmeal.fragment.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import java.math.BigDecimal;

public class IngredientAction {
    public static final IngredientAction CANCELED = new IngredientAction(Type.CANCELED, -1L, null);
    @NonNull
    private final Type type;
    private final long id;
    private final EditData dataOptional;

    private IngredientAction(@NonNull Type type, long id,
                     EditData dataOptional) {
        this.type = type;
        this.id = id;
        this.dataOptional = dataOptional;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public EditData getData() {
        return dataOptional;
    }

    @NonNull
    public static IngredientAction valueOf(@NonNull Type type, long id,
                                           @Nullable EditData editData) {
        if (type == Type.CANCELED) return CANCELED;
        return new IngredientAction(type, id, editData);
    }

    public static class EditData {
        @NonNull
        final IngredientTemplate ingredientTemplate;
        @NonNull
        final BigDecimal value;

        EditData(@NonNull IngredientTemplate ingredientTemplate, @NonNull BigDecimal value) {
            this.ingredientTemplate = ingredientTemplate;
            this.value = value;
        }

        public static EditData valueOf(@NonNull IngredientTemplate ingredientTemplate, @NonNull BigDecimal value) {
            return new EditData(ingredientTemplate, value);
        }

        @NonNull
        public IngredientTemplate getIngredientTemplate() {
            return ingredientTemplate;
        }

        @NonNull
        public BigDecimal getValue() {
            return value;
        }
    }

    public enum Type {
        NEW, EDIT, REMOVE, CANCELED
    }
}
