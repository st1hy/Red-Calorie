package com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

import org.parceler.Parcels;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class AddIngredientArgumentsModule {

    @Provides
    @Named("initialName")
    public static String provideInitialName(Intent intent) {
        String name = intent.getStringExtra(AddIngredientActivity.ARG_EXTRA_NAME);
        return name != null ? name : "";
    }

    @Provides
    @Nullable
    public static IngredientTemplate provideIngredientTemplate(Intent intent) {
        return Parcels.unwrap(intent.getParcelableExtra(AddIngredientActivity.ARG_EDIT_INGREDIENT_PARCEL));
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
}
