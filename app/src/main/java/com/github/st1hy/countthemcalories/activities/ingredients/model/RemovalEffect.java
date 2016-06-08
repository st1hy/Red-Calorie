package com.github.st1hy.countthemcalories.activities.ingredients.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Meal;

import java.util.List;

/**
 * Storage for removed objects as effect of IngredientTemplate deletion
 */
public class RemovalEffect {
    final IngredientTemplate template;
    final List<Ingredient> childIngredients;
    final List<JointIngredientTag> joins;
    final List<Meal> meals;

    public RemovalEffect(@NonNull IngredientTemplate template,
                         @NonNull List<Ingredient> childIngredients,
                         @NonNull List<JointIngredientTag> joins,
                         @NonNull List<Meal> meals) {
        this.template = template;
        this.childIngredients = childIngredients;
        this.joins = joins;
        this.meals = meals;
    }

    @NonNull
    public IngredientTemplate getTemplate() {
        return template;
    }

    @NonNull
    public List<Ingredient> getChildIngredients() {
        return childIngredients;
    }

    @NonNull
    public List<JointIngredientTag> getJointedTags() {
        return joins;
    }

    @NonNull
    public List<Meal> getMeals() {
        return meals;
    }
}
