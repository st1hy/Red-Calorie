package com.github.st1hy.countthemcalories.inject.activities.addingredient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientMenuAction;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreen;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreenImpl;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.fragment.AddIngredientFragmentComponentFactory;

import org.parceler.Parcels;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

import static com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity.ARG_EDIT_INGREDIENT_PARCEL;
import static com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity.ARG_EXTRA_NAME;


@Module
public class AddIngredientModule {
    public static final String EXTRA_NAME = "ingredient extra name";
    private final AddIngredientActivity activity;

    public AddIngredientModule(@NonNull AddIngredientActivity activity) {
        this.activity = activity;
    }

    @Provides
    public Activity activity() {
        return activity;
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
    }

    @Provides
    @Named("activityContext")
    public Context activityContext() {
        return activity;
    }

    @Provides
    public AddIngredientFragment provideContent(FragmentManager fragmentManager,
                                                AddIngredientFragmentComponentFactory componentFactory) {
        final String tag = "add ingredient content";

        AddIngredientFragment fragment = (AddIngredientFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new AddIngredientFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.add_ingredient_content_frame, fragment, tag)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        fragment.setComponentFactory(componentFactory);
        return fragment;
    }

    @Provides
    @Named("initialName")
    public String provideInitialName(Intent intent) {
        String name = intent.getStringExtra(ARG_EXTRA_NAME);
        return name != null ? name : "";
    }

    @Provides
    @Nullable
    public IngredientTemplate provideIngredientTemplate(Intent intent) {
        return Parcels.unwrap(intent.getParcelableExtra(ARG_EDIT_INGREDIENT_PARCEL));
    }

    @Provides
    public AmountUnitType provideUnitType(Intent intent) {
        String action = intent.getAction();
        if (AddIngredientType.DRINK.getAction().equals(action)) {
            return AmountUnitType.VOLUME;
        } else if (AddIngredientType.MEAL.getAction().equals(action)) {
            return AmountUnitType.MASS;
        }
        return AmountUnitType.MASS;
    }


    @Provides
    public FragmentManager provideFragmentManager() {
        return activity.getSupportFragmentManager();
    }

    @Provides
    public Intent provideIntent() {
        return activity.getIntent();
    }


    @PerActivity
    @Provides
    public PublishSubject<AddIngredientMenuAction> menuActions() {
        return PublishSubject.create();
    }

    @Provides
    public AddIngredientScreen addIngredientScreen(AddIngredientScreenImpl screen) {
        return screen;
    }

    @Provides
    public AddIngredientFragmentComponentFactory fragmentComponentFactory(AddIngredientComponent component) {
        return component;
    }

}
