package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;

import rx.Observable;

public interface AddMealView extends WithPictureView {
    void openOverviewActivity();

    void openAddIngredient();

    void setName(@NonNull String name);

    @NonNull Observable<CharSequence> getNameObservable();

    @NonNull Observable<Void> getAddIngredientObservable();
}
