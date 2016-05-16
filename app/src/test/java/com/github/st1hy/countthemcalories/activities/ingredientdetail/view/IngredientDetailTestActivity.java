package com.github.st1hy.countthemcalories.activities.ingredientdetail.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.DaggerIngredientDetailTestComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailTestComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailsModule;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsTestActivity;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.application.inject.ApplicationModule;
import com.github.st1hy.countthemcalories.application.inject.DbModelsModule;
import com.github.st1hy.countthemcalories.core.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.core.inject.DaggerApplicationTestComponent;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;

public class IngredientDetailTestActivity extends IngredientDetailsActivity {


    @NonNull
    protected IngredientDetailComponent getComponent(@Nullable Bundle savedInstanceState) {
        if (component == null) {
            CaloriesCounterApplication application = (CaloriesCounterApplication) getApplication();
            application.setComponent(DaggerApplicationTestComponent.builder()
                    .applicationModule(new ApplicationModule(application))
                    .databaseModule(new DatabaseModule())
                    .dbModelsModule(new DbModelsModule())
                    .build());
            component = DaggerIngredientDetailTestComponent.builder()
                    .applicationTestComponent((ApplicationTestComponent) getAppComponent())
                    .ingredientDetailsModule(new IngredientDetailsModule(this, savedInstanceState))
                    .build();
            prepareDb();
        }
        return component;
    }


    private void prepareDb() {
        IngredientDetailTestComponent component = (IngredientDetailTestComponent) this.component;
        DaoSession daoSession = component.getDaoSession();
        IngredientsTestActivity.addExampleIngredientsTagsAndJoin(daoSession);
    }
}
