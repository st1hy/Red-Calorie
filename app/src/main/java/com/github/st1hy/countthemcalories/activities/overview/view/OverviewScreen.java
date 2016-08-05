package com.github.st1hy.countthemcalories.activities.overview.view;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.core.command.view.UndoView;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;

import rx.Observable;

public interface OverviewScreen extends UndoView {

    @NonNull
    Observable<Void> getOpenMealScreenObservable();

    void openAddMealScreen();

    void openMealDetails(@NonNull MealParcel mealParcel, @NonNull View sharedView);

    void openEditMealScreen(@NonNull MealParcel mealParcel);

    void setTotalEnergy(@NonNull String energy);

    @NonNull
    Observable<MealDetailAction> getDetailScreenActionObservable();
}
