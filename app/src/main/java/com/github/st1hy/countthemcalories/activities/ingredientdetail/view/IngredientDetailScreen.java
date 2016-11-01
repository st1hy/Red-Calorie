package com.github.st1hy.countthemcalories.activities.ingredientdetail.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import java.math.BigDecimal;

public interface IngredientDetailScreen {

    void commitEditedIngredientChanges(long ingredientId,
                                       @NonNull IngredientTemplate ingredientTemplate,
                                       @NonNull BigDecimal amount);

    void removeIngredient(long ingredientId);

}
