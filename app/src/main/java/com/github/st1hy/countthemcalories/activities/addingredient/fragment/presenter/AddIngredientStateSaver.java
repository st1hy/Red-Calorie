package com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTagsModel;
import com.github.st1hy.countthemcalories.core.WithState;
import com.github.st1hy.countthemcalories.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import org.parceler.Parcels;

import javax.inject.Inject;

@PerFragment
public class AddIngredientStateSaver implements WithState {

    @NonNull
    private final IngredientTagsModel tagsModel;
    @NonNull
    private final AddIngredientModel ingredientModel;
    @NonNull
    private final PicturePicker picturePicker;

    @Inject
    AddIngredientStateSaver(@NonNull IngredientTagsModel tagsModel,
                            @NonNull AddIngredientModel ingredientModel,
                            @NonNull PicturePicker picturePicker) {
        this.tagsModel = tagsModel;
        this.ingredientModel = ingredientModel;
        this.picturePicker = picturePicker;
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(IngredientTagsModel.SAVED_TAGS_MODEL, Parcels.wrap(tagsModel));
        outState.putParcelable(AddIngredientModel.SAVED_INGREDIENT_MODEL, Parcels.wrap(ingredientModel));
        picturePicker.onSaveState(outState);
    }
}
