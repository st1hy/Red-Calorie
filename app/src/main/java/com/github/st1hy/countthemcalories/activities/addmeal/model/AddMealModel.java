package com.github.st1hy.countthemcalories.activities.addmeal.model;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.ui.withpicture.model.WithPictureModel;

import javax.inject.Inject;

public class AddMealModel extends WithPictureModel {

    @Inject
    public AddMealModel() {

    }

    @Override
    public int getImageSourceDialogTitleResId() {
        return R.string.add_meal_image_select_title;
    }

    @Override
    public int getImageSourceOptionArrayResId() {
        return R.array.add_meal_image_select_options;
    }
}
