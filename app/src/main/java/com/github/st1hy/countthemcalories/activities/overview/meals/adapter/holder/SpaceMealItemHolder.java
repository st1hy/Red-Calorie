package com.github.st1hy.countthemcalories.activities.overview.meals.adapter.holder;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.overview.meals.adapter.inject.MealItemRootView;
import com.github.st1hy.countthemcalories.activities.overview.meals.adapter.inject.PerMealRow;

import javax.inject.Inject;

@PerMealRow
public class SpaceMealItemHolder extends AbstractMealItemHolder {

    @Inject
    public SpaceMealItemHolder(@NonNull @MealItemRootView View itemView) {
        super(itemView);
    }
}
