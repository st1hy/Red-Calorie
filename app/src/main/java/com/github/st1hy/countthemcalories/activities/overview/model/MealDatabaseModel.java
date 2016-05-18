package com.github.st1hy.countthemcalories.activities.overview.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientDao;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.MealDao;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Provider;

import dagger.Lazy;
import dagger.internal.DoubleCheckLazy;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.CursorQuery;
import rx.Observable;

public class MealDatabaseModel extends RxDatabaseModel<Meal> {

    private final Lazy<MealDao> mealDaoLazy;

    public MealDatabaseModel(@NonNull Lazy<DaoSession> session) {
        super(session);
        mealDaoLazy = DoubleCheckLazy.create(new Provider<MealDao>() {
            @Override
            public MealDao get() {
                return session().getMealDao();
            }
        });
    }

    private MealDao dao() {
        return mealDaoLazy.get();
    }

    @Override
    public void performReadEntity(@NonNull Cursor cursor, @NonNull Meal output) {
        dao().readEntity(cursor, output, 0);
    }

    @NonNull
    public Observable<Void> addNew(@NonNull Meal meal, Collection<Ingredient> ingredients) {
        return fromDatabaseTask(addNewCall(meal, ingredients));
    }

    @NonNull
    private Callable<Void> addNewCall(final Meal meal, final Collection<Ingredient> ingredients) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                performInsert(meal);
                IngredientDao ingredientDao = session().getIngredientDao();
                for (Ingredient ingredient : ingredients) {
                    ingredient.setPartOfMeal(meal);
                    ingredientDao.insert(ingredient);
                }
                meal.refresh();
                meal.getIngredients();
                return null;
            }
        };
    }

    @NonNull
    @Override
    protected Meal performGetById(long id) {
        Meal meal = dao().load(id);
        meal.getIngredients();
        return meal;
    }

    @NonNull
    @Override
    protected Meal performInsert(@NonNull Meal meal) {
        dao().insert(meal);
        meal.getIngredients();
        return meal;
    }

    @Override
    protected void performRemove(@NonNull Meal data) {
        List<Ingredient> ingredients = data.getIngredients();
        data.delete();
        for (Ingredient ingredient: ingredients) {
            ingredient.delete();
        }
    }

    @Override
    protected void performRemoveAll() {
        dao().deleteAll();
    }

    @NonNull
    @Override
    protected CursorQuery allSortedByName() {
        return dao().queryBuilder()
                .orderAsc(MealDao.Properties.Name)
                .buildCursor();
    }

    @NonNull
    @Override
    protected CursorQuery filteredSortedByNameQuery() {
        return dao().queryBuilder()
                .where(MealDao.Properties.Name.like(""))
                .orderAsc(MealDao.Properties.Name)
                .buildCursor();
    }

    @Override
    protected long readKey(@NonNull Cursor cursor, int columnIndex) {
        return dao().readKey(cursor, columnIndex);
    }

    @NonNull
    @Override
    protected Property getKeyProperty() {
        return MealDao.Properties.Id;
    }

    @Override
    protected long getKey(Meal meal) {
        return meal.getId();
    }
}
