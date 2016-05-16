package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.ingredients.inject.DaggerIngredientsTestComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityModule;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsTestComponent;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsTestActivity;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.application.inject.ApplicationModule;
import com.github.st1hy.countthemcalories.application.inject.DbModelsModule;
import com.github.st1hy.countthemcalories.core.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.core.inject.DaggerApplicationTestComponent;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.JointIngredientTagDao;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

import org.joda.time.DateTime;

import static com.github.st1hy.countthemcalories.activities.tags.view.TagsTestActivity.exampleTags;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.getOrZero;

public class IngredientsTestActivity extends IngredientsActivity {

    public static final IngredientTemplate[] exampleIngredients = new IngredientTemplate[]{
            new IngredientTemplate(1L, "Ingredient 1", Uri.EMPTY, DateTime.now(), AmountUnitType.MASS,
                    getOrZero("20.5")), // kJ / g in database
            new IngredientTemplate(2L, "Ingredient 22", Uri.EMPTY, DateTime.now(), AmountUnitType.VOLUME,
                    getOrZero("6.04")), // kJ / ml in database
    };
    public static final JointIngredientTag[] exampleJoins = new JointIngredientTag[]{
            new JointIngredientTag(1L, exampleTags[0].getId(), exampleIngredients[0].getId()),
            new JointIngredientTag(2L, exampleTags[0].getId(), exampleIngredients[1].getId()),
            new JointIngredientTag(3L, exampleTags[1].getId(), exampleIngredients[1].getId()),
    };
    public static final IngredientTemplate additionalIngredient = new IngredientTemplate(3L, "Ingredient 23",
            Uri.EMPTY, DateTime.now(), AmountUnitType.VOLUME, getOrZero("2.04"));


    @NonNull
    @Override
    protected IngredientsActivityComponent getComponent() {
        if (component == null) {
            CaloriesCounterApplication application = (CaloriesCounterApplication) getApplication();
            application.setComponent(DaggerApplicationTestComponent.builder()
                    .applicationModule(new ApplicationModule(application))
                    .databaseModule(new DatabaseModule())
                    .dbModelsModule(new DbModelsModule())
                    .build());
            component = DaggerIngredientsTestComponent.builder()
                    .applicationTestComponent((ApplicationTestComponent) getAppComponent())
                    .ingredientsActivityModule(new IngredientsActivityModule(this))
                    .build();
            prepareDb();
        }
        return component;
    }


    private void prepareDb() {
        IngredientsTestComponent component = (IngredientsTestComponent) this.component;
        DaoSession daoSession = component.getDaoSession();
        addExampleIngredientsTagsAndJoin(daoSession);
    }

    public static void addExampleIngredientsTagsAndJoin(DaoSession session) {
        IngredientTemplateDao templateDao = session.getIngredientTemplateDao();
        JointIngredientTagDao jointIngredientTagDao = session.getJointIngredientTagDao();
        TagsTestActivity.addExampleTags(session);
        templateDao.deleteAll();
        jointIngredientTagDao.deleteAll();
        templateDao.insertInTx(exampleIngredients);
        jointIngredientTagDao.insertInTx(exampleJoins);
        session.clear();
        templateDao.loadAll();
    }
}
