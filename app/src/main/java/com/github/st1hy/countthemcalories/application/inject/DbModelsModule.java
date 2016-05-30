package com.github.st1hy.countthemcalories.application.inject;

import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDatabaseModel;
import com.github.st1hy.countthemcalories.activities.tags.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

@Module
public class DbModelsModule {

    @Provides
    @Singleton
    public RxTagsDatabaseModel provideTagsModel(Lazy<DaoSession> daoSession) {
        return new RxTagsDatabaseModel(daoSession);
    }

    @Provides
    @Singleton
    public IngredientTypesDatabaseModel provideIngredientsModel(Lazy<DaoSession> daoSession) {
        return new IngredientTypesDatabaseModel(daoSession);
    }

    @Provides
    @Singleton
    public MealDatabaseModel provideMealModel(Lazy<DaoSession> daoSession) {
        return new MealDatabaseModel(daoSession);
    }
}
