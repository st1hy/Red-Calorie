package com.github.st1hy.countthemcalories.activities.overview.model;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.database.Meal;

public class MealDetailParams {

    @NonNull
    private final Meal meal;
    @NonNull
    private final View sharedView;

    public MealDetailParams(@NonNull Meal meal, @NonNull View sharedView) {
        this.meal = meal;
        this.sharedView = sharedView;
    }

    @NonNull
    public Meal getMeal() {
        return meal;
    }

    @NonNull
    public View getSharedView() {
        return sharedView;
    }
}
