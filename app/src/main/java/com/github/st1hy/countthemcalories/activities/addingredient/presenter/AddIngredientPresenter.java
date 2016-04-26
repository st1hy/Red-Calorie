package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.support.annotation.IdRes;

import com.github.st1hy.countthemcalories.activities.withpicture.WithPicturePresenter;

public interface AddIngredientPresenter extends WithPicturePresenter {
    boolean onClickedOnAction(@IdRes int menuActionId);

}
