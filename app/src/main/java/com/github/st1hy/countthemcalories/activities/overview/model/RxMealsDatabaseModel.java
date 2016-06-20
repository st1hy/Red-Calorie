package com.github.st1hy.countthemcalories.activities.overview.model;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientDao;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.MealDao;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;

import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Provider;

import dagger.Lazy;
import dagger.internal.DoubleCheck;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.CursorQuery;
import de.greenrobot.dao.query.Query;
import rx.Observable;

public class RxMealsDatabaseModel extends RxDatabaseModel<Meal> {

    private final Lazy<MealDao> mealDaoLazy;
    private Query<Meal> filteredByDateSortedByDate;

    public RxMealsDatabaseModel(@NonNull Lazy<DaoSession> session) {
        super(session);
        mealDaoLazy = DoubleCheck.lazy(new Provider<MealDao>() {
            @Override
            public MealDao get() {
                return session().getMealDao();
            }
        });
    }

    @NonNull
    public Observable<Meal> unParcel(MealParcel mealParcel) {
        return mealParcel.getWhenReady().get(session());
    }

    private MealDao dao() {
        return mealDaoLazy.get();
    }

    @Override
    public void performReadEntity(@NonNull Cursor cursor, @NonNull Meal output) {
        dao().readEntity(cursor, output, 0);
    }

    @NonNull
    public Observable<Void> insertOrUpdate(@NonNull Meal meal, Collection<Ingredient> ingredients) {
        return fromDatabaseTask(insertOrUpdateCall(meal, ingredients));
    }

    @NonNull
    private Callable<Void> insertOrUpdateCall(final Meal meal, final Collection<Ingredient> ingredients) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                performInsertOrUpdate(meal, ingredients);
                return null;
            }
        };
    }

    @NonNull
    public Meal performInsertOrUpdate(@NonNull Meal meal, @NonNull Collection<Ingredient> ingredients) {
        Meal result = performInsert(meal);
        IngredientDao ingredientDao = session().getIngredientDao();
        List<Ingredient> mealIngredients = meal.getIngredients();
        for (Ingredient mealIngredient : mealIngredients) {
            if (!ingredients.contains(mealIngredient)) {
                IngredientTemplate ingredientType = mealIngredient.getIngredientType();
                List<Ingredient> childIngredients = ingredientType.getChildIngredients();
                mealIngredient.delete();
                childIngredients.remove(mealIngredient);
            }
        }
        for (Ingredient ingredient : ingredients) {
            ingredient.setPartOfMeal(meal);
            ingredientDao.insertOrReplace(ingredient);
        }
        meal.resetIngredients();
        meal.getIngredients();
        return result;
    }

    @NonNull
    public Observable<List<Meal>> getAllFilteredSortedDate(@NonNull final DateTime from, @NonNull final DateTime to) {
        return fromDatabaseTask(filteredBetween(from, to));
    }

    @NonNull
    private Callable<List<Meal>> filteredBetween(@NonNull final DateTime from, @NonNull final DateTime to) {
        return new Callable<List<Meal>>() {
            @Override
            public List<Meal> call() throws Exception {
                Query<Meal> query = filteredByDateSortedByDateSingleton().forCurrentThread();
                query.setParameter(0, from.getMillis());
                query.setParameter(1, to.getMillis());
                List<Meal> list = query.list();
                for (Meal meal : list) {
                    List<Ingredient> ingredients = meal.getIngredients();
                    for (Ingredient ingredient : ingredients) {
                        ingredient.getIngredientType();
                    }
                }
                return list;
            }
        };
    }

    @NonNull
    @Override
    public Meal performGetById(long id) {
        Meal meal = dao().load(id);
        meal.resetIngredients();
        meal.getIngredients();
        return meal;
    }

    @NonNull
    @Override
    protected Meal performInsert(@NonNull Meal meal) {
        dao().insertOrReplace(meal);
        meal.getIngredients();
        return meal;
    }

    @Override
    protected void performRemove(@NonNull Meal data) {
        performRemoveRaw(data);
    }

    @NonNull
    public Pair<Meal, List<Ingredient>> performRemoveRaw(@NonNull Meal data) {
        List<Ingredient> ingredients = data.getIngredients();
        data.delete();
        for (Ingredient ingredient: ingredients) {
            ingredient.delete();
        }
        return new Pair<>(data, ingredients);
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

    @NonNull
    protected Query<Meal> filteredByDateSortedByDate() {
        return dao().queryBuilder()
                .where(MealDao.Properties.CreationDate.between("", ""))
                .orderAsc(MealDao.Properties.CreationDate)
                .build();
    }

    @NonNull
    protected Query<Meal> filteredByDateSortedByDateSingleton() {
        if (filteredByDateSortedByDate == null) {
            filteredByDateSortedByDate = filteredByDateSortedByDate();
        }
        return filteredByDateSortedByDate;
    }

    @NonNull
    @Override
    protected Property getKeyProperty() {
        return MealDao.Properties.Id;
    }

    @Override
    protected long getKey(@NonNull Meal meal) {
        return meal.getId();
    }
}
