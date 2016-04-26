package com.github.st1hy.countthemcalories.activities.addmeal.model;

import com.github.st1hy.countthemcalories.database.Ingredient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MealIngredientsListModel {
    private final List<Ingredient> ingredients = new ArrayList<>(6);

    @Inject
    public MealIngredientsListModel() {
    }

    public int getItemsCount() {
        return ingredients.size();
    }

}
