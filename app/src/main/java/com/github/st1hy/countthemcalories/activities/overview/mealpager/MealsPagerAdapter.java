package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;

@PerActivity
public class MealsPagerAdapter extends FragmentStatePagerAdapter {

    @Inject
    MealsPagerComponent pagerComponent;

    @Inject
    public MealsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return pagerComponent.getMealsFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
