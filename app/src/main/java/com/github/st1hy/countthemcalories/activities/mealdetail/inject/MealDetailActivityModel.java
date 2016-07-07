package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import android.content.Intent;
import android.os.Bundle;

import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailFragment;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class MealDetailActivityModel {

    private final MealDetailActivity activity;

    public MealDetailActivityModel(MealDetailActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public MealDetailFragment provideDetailFragment(Bundle bundle) {
        MealDetailFragment fragment = new MealDetailFragment();
        fragment.setArguments(bundle);
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
}
