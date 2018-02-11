package com.github.st1hy.countthemcalories.database.rx;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTagJoint;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;

import java.util.List;

/**
 * Storage for removed objects as effect of IngredientTemplate deletion
 */
public class IngredientRemovalEffect {
    @NonNull
    private final IngredientTemplate template;
    @NonNull
    private final List<Ingredient> childIngredients;
    @NonNull
    private final List<IngredientTagJoint> joins;
    @NonNull
    private final List<Meal> meals;

    public IngredientRemovalEffect(@NonNull IngredientTemplate template,
                                   @NonNull List<Ingredient> childIngredients,
                                   @NonNull List<IngredientTagJoint> joins,
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
    public List<IngredientTagJoint> getJointedTags() {
        return joins;
    }

    @NonNull
    public List<Meal> getMeals() {
        return meals;
    }
}
