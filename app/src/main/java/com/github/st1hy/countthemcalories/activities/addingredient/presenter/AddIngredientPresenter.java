package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.ui.withpicture.presenter.WithPicturePresenter;

import rx.Observable;

public interface AddIngredientPresenter extends WithPicturePresenter {
    boolean onClickedOnAction(@IdRes int menuActionId);

    void onSelectUnitClicked();

    void onStart();

    void onSaveState(@NonNull Bundle outState);

    void onNameTextChanges(@NonNull Observable<CharSequence> observable);

    void onEnergyValueChanges(@NonNull Observable<CharSequence> observable);

}
