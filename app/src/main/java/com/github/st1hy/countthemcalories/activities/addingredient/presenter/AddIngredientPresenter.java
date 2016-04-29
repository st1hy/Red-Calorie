package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.withpicture.presenter.WithPicturePresenter;

import rx.Observable;

public interface AddIngredientPresenter extends WithPicturePresenter {
    boolean onClickedOnAction(@IdRes int menuActionId);

    void onSelectUnitClicked();

    void onUnitSelected(int which);

    void onStart();

    void onStop();

    void onSaveState(@NonNull Bundle outState);

    void onNameTextChanges(Observable<CharSequence> observable);
}
