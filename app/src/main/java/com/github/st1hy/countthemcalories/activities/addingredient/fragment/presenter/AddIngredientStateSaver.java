package com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTagsModel;
import com.github.st1hy.countthemcalories.core.WithState;

import org.parceler.Parcels;

import javax.inject.Inject;

public class AddIngredientStateSaver implements WithState {

    @NonNull
    private final IngredientTagsModel tagsModel;
    @NonNull
    private final AddIngredientModel ingredientModel;

    @Inject
    public AddIngredientStateSaver(@NonNull IngredientTagsModel tagsModel,
                                   @NonNull AddIngredientModel ingredientModel) {
        this.tagsModel = tagsModel;
        this.ingredientModel = ingredientModel;
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(IngredientTagsModel.SAVED_TAGS_MODEL, Parcels.wrap(tagsModel));
        outState.putParcelable(AddIngredientModel.SAVED_INGREDIENT_MODEL, Parcels.wrap(ingredientModel));
    }
}
