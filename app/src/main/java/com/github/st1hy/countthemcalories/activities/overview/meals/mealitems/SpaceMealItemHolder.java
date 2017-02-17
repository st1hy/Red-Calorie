package com.github.st1hy.countthemcalories.activities.overview.meals.mealitems;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.inject.activities.overview.meals.mealitems.PerMealRow;

import javax.inject.Inject;
import javax.inject.Named;

@PerMealRow
public class SpaceMealItemHolder extends AbstractMealItemHolder {
    @Inject
    public SpaceMealItemHolder(@NonNull @Named("mealItemRoot") View itemView) {
        super(itemView);
    }
}
