package com.github.st1hy.countthemcalories.activities.addmeal.fragment.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.google.common.base.Optional;

import java.math.BigDecimal;

public class IngredientAction {
    public static final IngredientAction CANCELED = new IngredientAction(Type.CANCELED, -1L,
            Optional.<EditData>absent());
    @NonNull
    final Type type;
    final long id;
    @NonNull
    final Optional<EditData> dataOptional;

    IngredientAction(@NonNull Type type, long id,
                     @NonNull Optional<EditData> dataOptional) {
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
    public Optional<EditData> getDataOptional() {
        return dataOptional;
    }

    @NonNull
    public static IngredientAction valueOf(@NonNull Type type, long id,
                                           @NonNull Optional<EditData> dataOptional) {
        if (type == Type.CANCELED) return CANCELED;
        return new IngredientAction(type, id, dataOptional);
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
