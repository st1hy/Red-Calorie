package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailScreen;

import rx.Observable;

public interface MealDetailView extends MealDetailScreen {

    void setName(@NonNull String name);

    @NonNull
    Observable<Void> getEditObservable();

    void setDate(@NonNull String date);

    void setEnergy(@NonNull String energy);

    Observable<Void> getDeleteObservable();

}
