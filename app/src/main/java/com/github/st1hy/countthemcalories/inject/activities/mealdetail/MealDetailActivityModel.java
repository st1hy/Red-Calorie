package com.github.st1hy.countthemcalories.inject.activities.mealdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.mealdetail.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.MealDetailFragment;

import dagger.Module;
import dagger.Provides;

@Module(includes = MealDetailActivityBindings.class)
public class MealDetailActivityModel {

    private final MealDetailActivity activity;
    private static final String FRAGMENT_TAG = "meal detail content";

    public MealDetailActivityModel(MealDetailActivity activity) {
        this.activity = activity;
    }

    @Provides
    public static MealDetailFragment provideDetailFragment(Bundle arguments,
                                                    FragmentManager fragmentManager,
                                                    MealDetailActivityComponent component) {

        MealDetailFragment fragment = (MealDetailFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new MealDetailFragment();
            fragment.setArguments(arguments);

            fragmentManager.beginTransaction()
                    .add(R.id.meal_detail_content_root, fragment, FRAGMENT_TAG)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(component);
        return fragment;
    }

    @Provides
    public static Bundle provideDetailFragmentData(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MealDetailFragment.ARG_MEAL_PARCEL,
                intent.getParcelableExtra(MealDetailActivity.EXTRA_MEAL_PARCEL));
        return bundle;
    }

    @Provides
    public static Intent provideIntent(Activity activity) {
        return activity.getIntent();
    }

    @Provides
    public static FragmentManager provideFragmentsManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
    }

}
