package com.github.st1hy.countthemcalories.activities.overview.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.github.st1hy.countthemcalories.database.Meal;

public class MealDetailParams {

    @NonNull
    private final Meal meal;
    @Nullable
    private final View sharedView;

    public MealDetailParams(@NonNull Meal meal, @Nullable View sharedView) {
        this.meal = meal;
        this.sharedView = sharedView;
    }

    @NonNull
    public Meal getMeal() {
        return meal;
    }

    @Nullable
    public View getSharedView() {
        return sharedView;
    }
}
