package com.github.st1hy.countthemcalories.activities.overview.meals.model;

import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;

import javax.inject.Inject;

public class MealsViewModel {

    @Inject
    public MealsViewModel() {
    }

    @StringRes
    public int getUndoRemoveMealMessage() {
        return R.string.overview_meal_removed_undo_message;
    }
}
