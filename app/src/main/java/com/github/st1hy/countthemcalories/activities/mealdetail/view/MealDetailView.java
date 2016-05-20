package com.github.st1hy.countthemcalories.activities.mealdetail.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import rx.Observable;

public interface MealDetailView {
    void finish();

    void setName(@NonNull String name);

    @NonNull
    ImageView getImageView();

    @NonNull
    Observable<Void> getEditObservable();

    void setDate(@NonNull String date);

    void setEnergy(@NonNull String energy);

    void setResultAndFinish(int resultCode, @NonNull Intent data);

    Observable<Void> getDeleteObservable();
}
