package com.github.st1hy.countthemcalories.activities.overview.meals.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.meals.model.CurrentDayModel;
import com.github.st1hy.countthemcalories.core.WithState;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import org.parceler.Parcels;

import javax.inject.Inject;

@PerFragment
public class MealsStateSaver implements WithState {

    public static final String CURRENT_DAY_STATE = "current day state";

    @Inject
    CurrentDayModel currentDayModel;

    @Inject
    public MealsStateSaver() {
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_DAY_STATE, Parcels.wrap(currentDayModel));
    }
}
