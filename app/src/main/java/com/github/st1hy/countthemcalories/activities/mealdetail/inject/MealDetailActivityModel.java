package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailFragment;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class MealDetailActivityModel {

    private final MealDetailActivity activity;

    public MealDetailActivityModel(MealDetailActivity activity) {
        this.activity = activity;
    }

    @Provides
    public MealDetailFragment provideDetailFragment(Bundle arguments, FragmentManager fragmentManager) {
        final String tag = "meal detail content";

        MealDetailFragment fragment = (MealDetailFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new MealDetailFragment();
            fragment.setArguments(arguments);

            fragmentManager.beginTransaction()
                    .add(R.id.meal_detail_content_root, fragment, tag)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    @Provides
    public Bundle provideDetailFragmentData(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MealDetailFragment.ARG_MEAL_PARCEL,
                intent.getParcelableExtra(MealDetailActivity.EXTRA_MEAL_PARCEL));
        return bundle;
    }

    @Provides
    public Intent provideIntent() {
        return activity.getIntent();
    }

    @Provides
    public FragmentManager provideFragmentsManager() {
        return activity.getSupportFragmentManager();
    }
}
