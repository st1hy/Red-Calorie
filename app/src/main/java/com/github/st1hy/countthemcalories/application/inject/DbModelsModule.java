package com.github.st1hy.countthemcalories.application.inject;

import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.database.DaoSession;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModelsModule {

    @Provides
    @Singleton
    public TagsModel provideTagsModel(DaoSession daoSession) {
        return new TagsModel(daoSession);
    }

    @Provides
    @Singleton
    public IngredientTypesModel provideIngredientsModel(DaoSession daoSession) {
        return new IngredientTypesModel(daoSession);
    }
}
