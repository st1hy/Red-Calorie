package com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems.PerMealRow;

import javax.inject.Inject;
import javax.inject.Named;

@PerMealRow
public class EmptyMealItemHolder extends AbstractMealItemHolder {

    @Inject
    public EmptyMealItemHolder(@NonNull @Named("mealItemRoot") View itemView) {
        super(itemView);
    }
}
