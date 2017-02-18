package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.activities.overview.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriod;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;

@PerActivity
public class MealsPagerAdapter extends FragmentStatePagerAdapter {

    @Inject
    MealsPagerComponent pagerComponent;
    @Nullable
    private TimePeriod model;

    @Inject
    public MealsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        MealsFragment mealsFragment = pagerComponent.getMealsFragment();
        if (model != null) {
            Bundle arguments = new Bundle();
            DayData day = model.getDayDataAt(position);
            arguments.putSerializable(MealsFragment.ARG_CURRENT_DATE, day.getDateTime());
            mealsFragment.setArguments(arguments);
        }
        return mealsFragment;
    }

    @Override
    public int getCount() {
        return model != null ? model.getCount() : 1;
    }

    public void updateModel(TimePeriod model) {
        this.model = model;
        notifyDataSetChanged();
    }
}
