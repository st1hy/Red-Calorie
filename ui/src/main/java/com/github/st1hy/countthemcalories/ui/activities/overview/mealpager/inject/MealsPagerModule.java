package com.github.st1hy.countthemcalories.ui.activities.overview.mealpager.inject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.overview.inject.OverviewActivityComponent;
import com.github.st1hy.countthemcalories.ui.activities.overview.mealpager.MealsPagerAdapter;
import com.github.st1hy.countthemcalories.ui.activities.overview.mealpager.PagerModel;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.ui.activities.overview.presenter.OverviewStateSaver;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.bundle.ActivitySavedState;

import org.parceler.Parcels;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class MealsPagerModule {

    @Binds
    public abstract PagerAdapter pagerAdapter(MealsPagerAdapter adapter);

    @Binds
    public abstract MealsPagerComponent pagerComponent(OverviewActivityComponent component);

    @Provides
    public static ViewPager viewPager(Activity activity, PagerAdapter adapter) {
        ViewPager viewPager = activity.findViewById(R.id.overview_view_pager);
        viewPager.setAdapter(adapter);
        return viewPager;
    }

    @Provides
    public static MealsFragment newMealsFragment() {
        return new MealsFragment();
    }

    @Provides
    @PerActivity
    public static PagerModel pagerModel(@Nullable @ActivitySavedState Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return Parcels.unwrap(savedInstanceState.getParcelable(OverviewStateSaver.SAVE_PAGE_STATE));
        } else {
            return new PagerModel();
        }
    }
}
