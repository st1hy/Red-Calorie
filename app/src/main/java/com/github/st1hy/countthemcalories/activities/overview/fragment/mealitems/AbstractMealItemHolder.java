package com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import rx.subjects.PublishSubject;

public abstract class AbstractMealItemHolder extends RecyclerView.ViewHolder {

    public AbstractMealItemHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void onAttached(@NonNull PublishSubject<MealInteraction> subject) {

    }

    public void onDetached() {

    }
}
