package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityModule;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealTestComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.DaggerAddMealTestComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsTestActivity;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.application.inject.ApplicationModule;
import com.github.st1hy.countthemcalories.application.inject.DbModelsModule;
import com.github.st1hy.countthemcalories.core.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.core.inject.DaggerApplicationTestComponent;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;

public class AddMealTestActivity extends AddMealActivity {

    @NonNull
    protected AddMealActivityComponent getComponent(@Nullable Bundle savedInstanceState) {
        if (component == null) {
            CaloriesCounterApplication application = (CaloriesCounterApplication) getApplication();
            application.setComponent(DaggerApplicationTestComponent.builder()
                    .applicationModule(new ApplicationModule(application))
                    .databaseModule(new DatabaseModule())
                    .dbModelsModule(new DbModelsModule())
                    .build());
            component = DaggerAddMealTestComponent.builder()
                    .applicationTestComponent((ApplicationTestComponent) getAppComponent())
                    .addMealActivityModule(new AddMealActivityModule(this, savedInstanceState))
                    .build();
            prepareDb();
        }
        return component;
    }


    private void prepareDb() {
        AddMealTestComponent component = (AddMealTestComponent) this.component;
        DaoSession daoSession = component.getDaoSession();
        IngredientsTestActivity.addExampleIngredientsTagsAndJoin(daoSession);
    }
}
