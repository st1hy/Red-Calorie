package com.github.st1hy.countthemcalories.activities.overview.model;

import android.support.annotation.NonNull;

public class MealDetailAction {
    public static final MealDetailAction CANCELED = create(Type.CANCELED, -1L);

    @NonNull
    private final Type type;
    private final long id;

    private MealDetailAction(@NonNull Type type, long id) {
        this.type = type;
        this.id = id;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public static MealDetailAction create(@NonNull Type type, long mealId) {
        return new MealDetailAction(type, mealId);
    }

    public enum Type {
        DELETE, EDIT, CANCELED
    }
}
