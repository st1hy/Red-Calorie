package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.st1hy.countthemcalories.activities.overview.mealpager.inject.MealsPagerComponent;
import com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.database.rx.timeperiod.TimePeriod;
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
            arguments.putInt(MealsFragment.ARG_CURRENT_PAGE, position);
            mealsFragment.setArguments(arguments);
        }
        return mealsFragment;
    }

    @Override
    public int getCount() {
        return model != null ? model.getDaysCount() : 0;
    }

    public void updatePages(TimePeriod model) {
        this.model = model;
        notifyDataSetChanged();
    }

    @Nullable
    public TimePeriod getModel() {
        return model;
    }
}
