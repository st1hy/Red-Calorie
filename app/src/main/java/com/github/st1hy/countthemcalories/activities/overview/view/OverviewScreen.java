package com.github.st1hy.countthemcalories.activities.overview.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.database.Meal;

import rx.Observable;

public interface OverviewScreen {

    @NonNull
    @CheckResult
    Observable<Void> getAddNewMealObservable();

    void addNewMeal();

    @NonNull
    @CheckResult
    Observable<MealDetailAction> openMealDetails(@NonNull Meal meal, @NonNull View sharedView);

    void editMeal(@NonNull Meal meal);

    void setTotalEnergy(@NonNull String energy);

}
