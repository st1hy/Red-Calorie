package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.WithState;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.database.Meal;

import org.parceler.Parcels;

import javax.inject.Inject;

@PerFragment
public class LifecycleController implements BasicLifecycle, WithState {

    public static final String SAVED_MEAL_STATE = "meal details model";

    @NonNull
    private final MealDetailPresenter presenter;
    @NonNull
    private final MealIngredientsPresenter ingredientsPresenter;
    @NonNull
    private final Meal meal;

    @Inject
    public LifecycleController(@NonNull MealDetailPresenter presenter,
                               @NonNull MealIngredientsPresenter ingredientsPresenter,
                               @NonNull Meal meal) {
        this.presenter = presenter;
        this.ingredientsPresenter = ingredientsPresenter;
        this.meal = meal;
    }

    @Override
    public void onStart() {
        presenter.onStart();
        ingredientsPresenter.onStart();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        ingredientsPresenter.onStop();
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_MEAL_STATE, Parcels.wrap(meal));
    }
}
