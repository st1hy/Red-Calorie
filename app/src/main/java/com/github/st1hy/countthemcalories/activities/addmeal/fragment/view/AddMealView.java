package com.github.st1hy.countthemcalories.activities.addmeal.fragment.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.core.state.Visibility;

import rx.Observable;

public interface AddMealView extends AddMealScreen {

    void onMealSaved();

    void setName(@NonNull String name);

    @NonNull
    @CheckResult
    Observable<CharSequence> getNameObservable();

    void setEmptyIngredientsVisibility(@NonNull Visibility visibility);

    void scrollTo(int itemPosition);

    void setTotalEnergy(@NonNull String totalEnergy);

    void showNameError(@NonNull String error);

    void hideNameError();

}
