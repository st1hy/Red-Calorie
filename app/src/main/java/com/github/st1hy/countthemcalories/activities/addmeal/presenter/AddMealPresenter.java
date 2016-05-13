package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenter;

public interface AddMealPresenter extends WithPicturePresenter {

    void onStart();

    void onSaveState(@NonNull Bundle outState);

    boolean onClickedOnAction(@IdRes int menuActionId);

    /**
     * @return true if result was handled
     */
    boolean handleActivityResult(int requestCode, int resultCode, Intent data);
}
