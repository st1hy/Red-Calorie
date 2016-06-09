package com.github.st1hy.countthemcalories.application.inject;

import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.model.RxMealsDatabaseModel;
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
    public RxIngredientsDatabaseModel provideIngredientsModel(Lazy<DaoSession> daoSession) {
        return new RxIngredientsDatabaseModel(daoSession);
    }

    @Provides
    @Singleton
    public RxMealsDatabaseModel provideMealModel(Lazy<DaoSession> daoSession) {
        return new RxMealsDatabaseModel(daoSession);
    }
}
