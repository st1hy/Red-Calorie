package com.github.st1hy.countthemcalories.activities.overview.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailParams;
import com.github.st1hy.countthemcalories.database.Meal;

import rx.Observable;
import rx.functions.Func1;

public interface OverviewScreen {

    @NonNull
    @CheckResult
    Observable.Transformer<MealDetailParams, MealDetailAction> openMealDetails();

    void editMeal(@NonNull Meal meal);

    @NonNull
    @CheckResult
    Observable<MotionEvent> touchOverlay(@NonNull Func1<? super MotionEvent, Boolean> handled);

    void closeFloatingMenu();

    boolean isFabMenuOpen();

    void copyMeal(@NonNull Meal meal);
}
