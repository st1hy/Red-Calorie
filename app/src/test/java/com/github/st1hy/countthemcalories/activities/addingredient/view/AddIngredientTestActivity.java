package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientModule;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientTestComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.DaggerAddIngredientTestComponent;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.application.inject.ApplicationModule;
import com.github.st1hy.countthemcalories.application.inject.DbModelsModule;
import com.github.st1hy.countthemcalories.core.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.core.inject.DaggerApplicationTestComponent;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.JointIngredientTagDao;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;

import static org.junit.Assert.assertEquals;

public class AddIngredientTestActivity extends AddIngredientActivity {
    public static Tag[] exampleTags = new Tag[] {new Tag(1L, "Test tag"), new Tag(2L, "Tag2"), new Tag(3L, "meal")};

    @NonNull
    @Override
    protected AddIngredientComponent getComponent(@Nullable Bundle savedInstanceState) {
        if (component == null) {
            CaloriesCounterApplication application = (CaloriesCounterApplication) getApplication();
            application.setComponent(DaggerApplicationTestComponent.builder()
                    .applicationModule(new ApplicationModule(application))
                    .databaseModule(new DatabaseModule())
                    .dbModelsModule(new DbModelsModule())
                    .build());

            component = DaggerAddIngredientTestComponent.builder()
                    .applicationTestComponent((ApplicationTestComponent) getAppComponent())
                    .addIngredientModule(new AddIngredientModule(this, savedInstanceState))
                    .build();

            prepareDb();
        }
        return component;
    }


    private void prepareDb() {
        AddIngredientTestComponent component = (AddIngredientTestComponent) this.component;
        DaoSession daoSession = component.getDaoSession();
        TagDao tagDao = daoSession.getTagDao();
        tagDao.deleteAll();
        tagDao.insertOrReplaceInTx(exampleTags);
        assertEquals(3, tagDao.loadAll().size());
        IngredientTemplateDao templateDao = daoSession.getIngredientTemplateDao();
        templateDao.deleteAll();
        assertEquals(0, templateDao.loadAll().size());
        JointIngredientTagDao jointIngredientTagDao = daoSession.getJointIngredientTagDao();
        jointIngredientTagDao.deleteAll();
        assertEquals(0, jointIngredientTagDao.loadAll().size());
    }
}
