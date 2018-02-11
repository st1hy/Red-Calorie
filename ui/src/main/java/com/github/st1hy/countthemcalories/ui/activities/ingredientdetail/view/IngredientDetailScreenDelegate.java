package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Ingredient;

public class IngredientDetailScreenDelegate implements IngredientDetailScreen {

    @NonNull
    private final IngredientDetailScreen screen;

    public IngredientDetailScreenDelegate(@NonNull IngredientDetailScreen screen) {
        this.screen = screen;
    }

    @NonNull
    protected IngredientDetailScreen getScreen() {
        return screen;
    }

    @Override
    public void finishEdit(long ingredientId, @NonNull Ingredient ingredient) {
        getScreen().finishEdit(ingredientId, ingredient);
    }

    @Override
    public void finishRemove(long ingredientId) {
        getScreen().finishRemove(ingredientId);
    }
}
