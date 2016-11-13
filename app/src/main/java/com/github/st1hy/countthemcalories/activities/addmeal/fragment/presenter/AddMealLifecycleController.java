package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import javax.inject.Inject;

@PerFragment
public class AddMealLifecycleController implements BasicLifecycle {

    @NonNull
    private final IngredientsListPresenter listPresenter;
    @NonNull
    private final AddMealPresenter mealPresenter;

    @Inject
    public AddMealLifecycleController(@NonNull IngredientsListPresenter listPresenter,
                                      @NonNull AddMealPresenter mealPresenter) {
        this.listPresenter = listPresenter;
        this.mealPresenter = mealPresenter;
    }


    @Override
    public void onStart() {
        mealPresenter.onStop();
        listPresenter.onStart();
    }

    @Override
    public void onStop() {
        mealPresenter.onStop();
        listPresenter.onStop();
    }
}
