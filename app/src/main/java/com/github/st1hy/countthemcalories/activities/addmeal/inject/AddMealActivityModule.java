package com.github.st1hy.countthemcalories.activities.addmeal.inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class AddMealActivityModule {
    private final AddMealActivity activity;

    public AddMealActivityModule(@NonNull AddMealActivity activity) {
        this.activity = activity;
    }


    @Provides
    public AddMealFragment provideContent(FragmentManager fragmentManager, Bundle arguments) {
        final String tag = "add meal content";

        AddMealFragment fragment = (AddMealFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new AddMealFragment();
            fragment.setArguments(arguments);

            fragmentManager.beginTransaction()
                    .add(R.id.add_meal_content_frame, fragment, tag)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        }
        return fragment;
    }

    @Provides
    public FragmentManager provideFragmentManager() {
        return activity.getSupportFragmentManager();
    }

    @Provides
    public Bundle provideArguments(Intent intent) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(AddMealFragmentModule.EXTRA_MEAL_PARCEL,
                intent.getParcelableExtra(AddMealFragmentModule.EXTRA_MEAL_PARCEL));
        arguments.putParcelable(AddMealFragmentModule.EXTRA_INGREDIENT_PARCEL,
                intent.getParcelableExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL));
        return arguments;
    }

    @Provides
    public Intent provideIntent() {
        return activity.getIntent();
    }
}
