package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.ui.withpicture.view.WithPictureView;

public interface AddIngredientView extends WithPictureView {
    void openIngredientsScreen();

    void setSelectedUnitName(@NonNull String unitName);

    void setName(@NonNull String name);

    void setEnergyDensityValue(@NonNull String energyValue);

    void openSelectTagScreen();
}
