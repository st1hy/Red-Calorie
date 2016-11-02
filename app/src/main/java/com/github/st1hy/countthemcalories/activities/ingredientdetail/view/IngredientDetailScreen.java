package com.github.st1hy.countthemcalories.activities.ingredientdetail.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Ingredient;

public interface IngredientDetailScreen {

    void commitEditedIngredientChanges(long ingredientId,
                                       @NonNull Ingredient ingredient);

    void removeIngredient(long ingredientId);

}
