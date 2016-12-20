package com.github.st1hy.countthemcalories.activities.overview.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailParams;
import com.github.st1hy.countthemcalories.database.Meal;

import rx.Observable;

public interface OverviewScreen {

    @NonNull
    @CheckResult
    Observable<Void> getAddNewMealObservable();

    void addNewMeal();

    @NonNull
    @CheckResult
    Observable.Transformer<MealDetailParams, MealDetailAction> openMealDetails();

    void editMeal(@NonNull Meal meal);

    void setTotalEnergy(@NonNull String energy);

}
