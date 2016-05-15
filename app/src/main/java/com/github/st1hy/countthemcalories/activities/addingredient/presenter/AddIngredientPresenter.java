package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenter;

public interface AddIngredientPresenter extends WithPicturePresenter {
    void onStart();

    void onSaveState(@NonNull Bundle outState);

    boolean onClickedOnAction(int itemId);
}
