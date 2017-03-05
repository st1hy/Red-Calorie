package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientMenuAction;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreen;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreenImpl;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import org.parceler.Parcels;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

import static com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity.ARG_EDIT_INGREDIENT_PARCEL;
import static com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity.ARG_EXTRA_NAME;

@Module
public abstract class AddIngredientModule {
    private static final String CONTENT_TAG = "add ingredient content";

    @Binds
    public abstract AddIngredientScreen addIngredientScreen(AddIngredientScreenImpl screen);

    @Binds
    public abstract AddIngredientFragmentComponentFactory fragmentComponentFactory(
            AddIngredientComponent component);

    @Provides
    public static AddIngredientFragment provideContent(
            FragmentManager fragmentManager,
            AddIngredientFragmentComponentFactory componentFactory) {

        AddIngredientFragment fragment = (AddIngredientFragment) fragmentManager
                .findFragmentByTag(CONTENT_TAG);
        if (fragment == null) {
            fragment = new AddIngredientFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.add_ingredient_content_frame, fragment, CONTENT_TAG)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(componentFactory);
        return fragment;
    }

    @Provides
    @Named("initialName")
    public static String provideInitialName(Intent intent) {
        String name = intent.getStringExtra(ARG_EXTRA_NAME);
        return name != null ? name : "";
    }

    @Provides
    @Nullable
    public static IngredientTemplate provideIngredientTemplate(Intent intent) {
        return Parcels.unwrap(intent.getParcelableExtra(ARG_EDIT_INGREDIENT_PARCEL));
    }

    @Provides
    public static AmountUnitType provideUnitType(Intent intent) {
        String action = intent.getAction();
        if (AddIngredientType.DRINK.getAction().equals(action)) {
            return AmountUnitType.VOLUME;
        } else if (AddIngredientType.MEAL.getAction().equals(action)) {
            return AmountUnitType.MASS;
        }
        return AmountUnitType.MASS;
    }

    @PerActivity
    @Provides
    public static PublishSubject<AddIngredientMenuAction> menuActions() {
        return PublishSubject.create();
    }
}
