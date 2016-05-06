package com.github.st1hy.countthemcalories.activities.ingredients.model;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;

import javax.inject.Inject;

public class IngredientsActivityModel {
    private final Intent intent;

    @Inject
    public IngredientsActivityModel(@Nullable Intent intent) {
        this.intent = intent;
    }

    public boolean isInSelectMode() {
        return intent != null && IngredientsActivity.ACTION_SELECT_INGREDIENT.equals(intent.getAction());
    }
}
