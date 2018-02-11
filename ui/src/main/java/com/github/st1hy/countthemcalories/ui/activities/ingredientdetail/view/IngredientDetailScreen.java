package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Ingredient;

public interface IngredientDetailScreen {

    void finishEdit(long ingredientId,
                    @NonNull Ingredient ingredient);

    void finishRemove(long ingredientId);
}
