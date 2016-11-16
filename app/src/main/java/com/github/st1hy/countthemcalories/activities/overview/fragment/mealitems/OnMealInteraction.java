package com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems;

import android.support.annotation.NonNull;

public interface OnMealInteraction {
    void onMealClicked(@NonNull MealItemHolder holder);

    void onDeleteClicked(@NonNull MealItemHolder holder);

    void onEditClicked(@NonNull MealItemHolder holder);
}
