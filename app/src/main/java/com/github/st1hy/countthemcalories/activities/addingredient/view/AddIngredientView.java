package com.github.st1hy.countthemcalories.activities.addingredient.view;

import com.github.st1hy.countthemcalories.core.ui.withpicture.view.WithPictureView;

public interface AddIngredientView extends WithPictureView {
    void openIngredientsScreen();

    void setSelectedUnitName(String unitName);
}
