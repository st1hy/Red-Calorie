package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import android.support.v4.view.ViewPager;

import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;

@PerActivity
public class MealPagerView {

    @Inject
    ViewPager viewPager;

    @Inject
    public MealPagerView() {
    }

}
