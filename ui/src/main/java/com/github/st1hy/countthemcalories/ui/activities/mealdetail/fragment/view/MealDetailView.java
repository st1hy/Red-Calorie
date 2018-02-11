package com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.mealdetail.view.MealDetailScreen;

import rx.Observable;

public interface MealDetailView extends MealDetailScreen {

    void setName(@NonNull String name);

    @NonNull
    @CheckResult
    Observable<Void> getEditObservable();

    void setDate(@NonNull String date);

    void setEnergy(@NonNull String energy);

    @NonNull
    @CheckResult
    Observable<Void> getDeleteObservable();

    @NonNull
    @CheckResult
    Observable<Void> getCopyObservable();
}
