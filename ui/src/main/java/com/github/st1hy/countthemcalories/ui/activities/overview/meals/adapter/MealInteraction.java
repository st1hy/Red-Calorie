package com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.holder.MealItemHolder;

import rx.functions.Func1;

public class MealInteraction {

    @NonNull
    private final Type type;
    @NonNull
    private final MealItemHolder holder;

    public MealInteraction(@NonNull Type type, @NonNull MealItemHolder holder) {
        this.type = type;
        this.holder = holder;
    }

    @NonNull
    public static MealInteraction of(@NonNull Type type, @NonNull MealItemHolder holder) {
        return new MealInteraction(type, holder);
    }

    @NonNull
    @CheckResult
    public static Func1<MealInteraction, Boolean> ofType(@NonNull final Type type) {
        return interaction -> interaction.getType() == type;
    }

    public enum Type {
        OPEN, DELETE, EDIT
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @NonNull
    public MealItemHolder getHolder() {
        return holder;
    }
}
