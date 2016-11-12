package com.github.st1hy.countthemcalories.activities.overview.view;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.core.command.undo.UndoViewImpl;
import com.github.st1hy.countthemcalories.database.Meal;

import rx.Observable;

public interface OverviewScreen extends UndoViewImpl {

    @NonNull
    Observable<Void> getOpenMealScreenObservable();

    void openAddMealScreen();

    void openMealDetails(@NonNull Meal meal, @NonNull View sharedView);

    void openEditMealScreen(@NonNull Meal meal);

    void setTotalEnergy(@NonNull String energy);

    @NonNull
    Observable<MealDetailAction> getDetailScreenActionObservable();
}
