package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;
import com.github.st1hy.countthemcalories.database.property.BigDecimalPropertyConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

public class MealIngredientsListModel {

    final RxMealsDatabaseModel databaseModel;
    final RxIngredientsDatabaseModel ingredientTypesModel;

    final ArrayList<Ingredient> ingredients;
    final ParcelableProxy parcelableProxy;

    final Observable<Integer> loadingObservable;

    public MealIngredientsListModel(@NonNull RxIngredientsDatabaseModel ingredientTypesModel,
                                    @NonNull RxMealsDatabaseModel databaseModel,
                                    @Nullable Intent intent,
                                    @Nullable Bundle savedState) {
        this.ingredientTypesModel = ingredientTypesModel;
        this.databaseModel = databaseModel;
        ParcelableProxy parcelableProxy = null;
        if (savedState != null) {
            parcelableProxy = savedState.getParcelable(ParcelableProxy.STATE_MODEL);
        }
        if (parcelableProxy != null) {
            if (parcelableProxy.isReCreated) {
                ingredients = new ArrayList<>(5);
                loadingObservable = loadItems(parcelableProxy.ingredients);
            } else {
                ingredients = new ArrayList<>(parcelableProxy.ingredients);
                loadingObservable = Observable.empty();
            }
        } else {
            parcelableProxy = new ParcelableProxy();
            ingredients = new ArrayList<>(5);
            Observable<Integer> observable = null;
            if (intent != null) {
                MealParcel parcel = intent.getParcelableExtra(AddMealActivity.EXTRA_MEAL_PARCEL);
                if (parcel != null) {
                    observable = loadItems(parcel);
                }
            }
            if (observable == null) observable = Observable.empty();
            loadingObservable = observable;
        }
        this.parcelableProxy = parcelableProxy;
    }

    @NonNull
    public Ingredient getItemAt(int position) {
        return ingredients.get(position);
    }

    public int getItemsCount() {
        return ingredients.size();
    }

    /**
     * @return notifies that items have been loaded to memory, on main thread or immediate
     */
    @NonNull
    public Observable<Integer> getItemsLoadedObservable() {
        return loadingObservable;
    }

    /**
     * @return notifies when item has been loaded from database and what is its position, on main thread
     */
    @NonNull
    public Observable<Integer> addIngredientOfType(@NonNull IngredientTypeParcel typeParcel,
                                                   @NonNull final BigDecimal amount) {
        return unparcel(typeParcel).observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<IngredientTemplate, Ingredient>() {
                    @Override
                    public Ingredient call(IngredientTemplate ingredientTemplate) {
                        Ingredient ingredient = new Ingredient();
                        ingredient.setIngredientType(ingredientTemplate);
                        ingredient.setAmount(amount);
                        return ingredient;
                    }
                })
                .map(onIngredient());
    }

    @NonNull
    public Observable<Integer> modifyIngredient(final int position,
                                                @NonNull IngredientTypeParcel typeParcel,
                                                @NonNull final BigDecimal amount) {
        if (position >= ingredients.size() || position < 0)
            throw new ArrayIndexOutOfBoundsException("Cannot modify non-existing ingredient");
        final Ingredient source = ingredients.get(position);
        return unparcel(typeParcel).observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<IngredientTemplate, Integer>() {
                    @Override
                    public Integer call(IngredientTemplate ingredientTemplate) {
                        source.setIngredientType(ingredientTemplate);
                        source.setAmount(amount);
                        return position;
                    }
                });
    }

    @NonNull
    public Observable<IngredientTemplate> unparcel(@NonNull IngredientTypeParcel typeParcel) {
        return ingredientTypesModel.unParcel(typeParcel);
    }

    public void removeIngredient(int position) {
        if (position >= ingredients.size() || position < 0)
            throw new ArrayIndexOutOfBoundsException("Cannot remove non-existing ingredient");
        ingredients.remove(position);
    }

    @NonNull
    Observable<Integer> loadItems(@NonNull List<Ingredient> ingredients) {
        Observable<Ingredient> notLoaded = Observable.from(ingredients);
        Observable<Integer> loading = notLoaded
                .concatMap(new Func1<Ingredient, Observable<IngredientTemplate>>() {
                    @Override
                    public Observable<IngredientTemplate> call(Ingredient ingredient) {
                        return ingredientTypesModel.getById(ingredient.getIngredientTypeId());
                    }
                }).zipWith(notLoaded, new Func2<IngredientTemplate, Ingredient, Ingredient>() {
                    @Override
                    public Ingredient call(IngredientTemplate ingredientTemplate, Ingredient ingredient) {
                        ingredient.setIngredientType(ingredientTemplate);
                        return ingredient;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .map(onIngredient())
                .replay().autoConnect().share();
        loading.subscribe(new Loading());
        return loading;
    }

    @NonNull
    Observable<Integer> loadItems(@NonNull MealParcel mealParcel) {
        Observable<Integer> loading = databaseModel.unParcel(mealParcel)
                .map(new Func1<Meal, List<Ingredient>>() {
                    @Override
                    public List<Ingredient> call(Meal meal) {
                        return meal.getIngredients();
                    }
                })
                .concatMap(new Func1<List<Ingredient>, Observable<Ingredient>>() {
                    @Override
                    public Observable<Ingredient> call(List<Ingredient> ingredients) {
                        return Observable.from(ingredients);
                    }
                })
                .map(onIngredient())
                .replay().autoConnect().share();
        loading.subscribe(new Loading());
        return loading;
    }

    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(ParcelableProxy.STATE_MODEL, parcelableProxy.snapshot(this));
    }

    @NonNull
    private Func1<Ingredient, Integer> onIngredient() {
        return new Func1<Ingredient, Integer>() {
            @Override
            public Integer call(Ingredient ingredient) {
                ingredients.add(ingredient);
                return ingredients.size() - 1;
            }
        };
    }

    public int indexOf(@NonNull Ingredient ingredient) {
        return ingredients.indexOf(ingredient);
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public static class Loading extends Subscriber<Integer> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Ingredient loading failed");
        }

        @Override
        public void onNext(Integer itemPosition) {
            Timber.v("Ingredient loaded");
        }
    }

    static class ParcelableProxy implements Parcelable {
        private static final BigDecimalPropertyConverter decimalConverter = new BigDecimalPropertyConverter();
        static String STATE_MODEL = "add meal ingredients model";
        ArrayList<Ingredient> ingredients;
        boolean isReCreated = false;

        ParcelableProxy() {
        }

        ParcelableProxy snapshot(@NonNull MealIngredientsListModel model) {
            isReCreated = false;
            this.ingredients = new ArrayList<>(model.ingredients);
            return this;
        }

        public static final Creator<ParcelableProxy> CREATOR = new Creator<ParcelableProxy>() {
            @Override
            public ParcelableProxy createFromParcel(Parcel source) {
                ParcelableProxy parcelableProxy = new ParcelableProxy();
                parcelableProxy.isReCreated = true;
                final int size = source.readInt();
                ArrayList<Ingredient> ingredients = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    long ingredientId = source.readLong();
                    long ingredientTypeId = source.readLong();
                    BigDecimal value = decimalConverter.convertToEntityProperty(source.readString());
                    Ingredient ingredient = new Ingredient();
                    if (ingredientId != -1L) ingredient.setId(ingredientId);
                    ingredient.setAmount(value);
                    ingredient.setIngredientTypeId(ingredientTypeId);
                    ingredients.add(ingredient);
                }
                parcelableProxy.ingredients = ingredients;
                return parcelableProxy;
            }

            @Override
            public ParcelableProxy[] newArray(int size) {
                return new ParcelableProxy[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(ingredients.size());
            for (Ingredient ingredient : ingredients) {
                Long id = ingredient.getId();
                if (id == null) id = -1L;
                dest.writeLong(id);
                dest.writeLong(ingredient.getIngredientTypeId());
                dest.writeString(decimalConverter.convertToDatabaseValue(ingredient.getAmount()));
            }
        }
    }
}
