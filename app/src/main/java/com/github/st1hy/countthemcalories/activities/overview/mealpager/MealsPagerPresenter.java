package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;

@PerActivity
public class MealsPagerPresenter implements BasicLifecycle {

    @Inject
    MealPagerView view;

    @Inject
    public MealsPagerPresenter() {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {

    }

}
