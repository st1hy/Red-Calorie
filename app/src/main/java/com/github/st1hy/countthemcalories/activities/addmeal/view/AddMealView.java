package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.ui.view.BaseView;

public interface AddMealView extends BaseView {
    void openOverviewActivity();

    void showSelectImageInputDialog();

    void openCameraAndGetPicture();

    void pickImageFromGallery();

    void setImageToView(@NonNull Uri uri);

    void openAddIngredient();
}
