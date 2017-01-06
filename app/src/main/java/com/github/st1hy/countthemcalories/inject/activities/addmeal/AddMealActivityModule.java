package com.github.st1hy.countthemcalories.inject.activities.addmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreenImpl;
import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.AddMealFragmentModule;

import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

@Module(includes = AddMealActivityBindings.class)
public class AddMealActivityModule {
    private final AddMealActivity activity;
    private static final String ADD_MEAL_CONTENT = "add meal content";

    public AddMealActivityModule(@NonNull AddMealActivity activity) {
        this.activity = activity;
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
    }

    @Provides
    public static AddMealFragment provideContent(FragmentManager fragmentManager,
                                                 Bundle arguments,
                                                 AddMealActivityComponent component) {

        AddMealFragment fragment = (AddMealFragment) fragmentManager.findFragmentByTag(ADD_MEAL_CONTENT);
        if (fragment == null) {
            fragment = new AddMealFragment();
            fragment.setArguments(arguments);

            fragmentManager.beginTransaction()
                    .add(R.id.add_meal_content_frame, fragment, ADD_MEAL_CONTENT)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(component);
        return fragment;
    }

    @Provides
    public static FragmentManager provideFragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @Provides
    public static Bundle provideArguments(Intent intent) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(AddMealFragmentModule.EXTRA_MEAL_PARCEL,
                intent.getParcelableExtra(AddMealFragmentModule.EXTRA_MEAL_PARCEL));
        arguments.putParcelable(AddMealFragmentModule.EXTRA_INGREDIENT_PARCEL,
                intent.getParcelableExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL));
        return arguments;
    }

    @Provides
    public static Intent provideIntent(Activity activity) {
        return activity.getIntent();
    }

    @Provides
    @PerActivity
    public static PublishSubject<AddMealMenuAction> menuActionPublishSubject() {
        return PublishSubject.create();
    }

}
