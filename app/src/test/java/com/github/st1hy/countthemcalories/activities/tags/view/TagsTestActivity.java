package com.github.st1hy.countthemcalories.activities.tags.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.inject.DaggerTagsTestComponent;
import com.github.st1hy.countthemcalories.activities.tags.inject.TagsComponent;
import com.github.st1hy.countthemcalories.activities.tags.inject.TagsModule;
import com.github.st1hy.countthemcalories.activities.tags.inject.TagsTestComponent;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.application.inject.ApplicationModule;
import com.github.st1hy.countthemcalories.application.inject.DbModelsModule;
import com.github.st1hy.countthemcalories.core.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.core.inject.DaggerApplicationTestComponent;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;

import static org.junit.Assert.assertEquals;

public class TagsTestActivity extends TagsActivity {

    @NonNull
    @Override
    protected TagsComponent getComponent() {
        if (component == null) {
            CaloriesCounterApplication application = (CaloriesCounterApplication) getApplication();
            application.setComponent(DaggerApplicationTestComponent.builder()
                    .applicationModule(new ApplicationModule(application))
                    .databaseModule(new DatabaseModule())
                    .dbModelsModule(new DbModelsModule())
                    .build());
            component = DaggerTagsTestComponent.builder()
                    .applicationTestComponent((ApplicationTestComponent) getAppComponent())
                    .tagsModule(new TagsModule(this))
                    .build();
            prepareDb();
        }
        return component;
    }


    private void prepareDb() {
        TagsTestComponent component = (TagsTestComponent) this.component;
        DaoSession daoSession = component.getDaoSession();
        TagDao tagDao = daoSession.getTagDao();
        tagDao.deleteAll();
        tagDao.insertInTx(new Tag(null, "Test tag"), new Tag(null, "Tag2"), new Tag(null, "meal"));
        assertEquals(3, tagDao.loadAll().size());
    }
}
