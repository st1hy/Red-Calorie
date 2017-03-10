package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.MealDetailFragment;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailScreen;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailScreenImpl;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class MealDetailActivityModel {

    private static final String FRAGMENT_TAG = "meal detail content";

    @Binds
    public abstract MealDetailScreen mealDetailScreen(MealDetailScreenImpl screen);

    @Provides
    public static MealDetailFragment provideDetailFragment(FragmentManager fragmentManager,
                                                           MealDetailActivityComponent component) {

        MealDetailFragment fragment = (MealDetailFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new MealDetailFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.meal_detail_content_root, fragment, FRAGMENT_TAG)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(component);
        return fragment;
    }
}
