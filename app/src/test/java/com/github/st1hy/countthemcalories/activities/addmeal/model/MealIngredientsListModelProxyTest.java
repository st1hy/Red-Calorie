package com.github.st1hy.countthemcalories.activities.addmeal.model;


import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDatabaseModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.github.st1hy.countthemcalories.testutils.SimpleSubscriber;
import com.github.st1hy.countthemcalories.testutils.TimberUtils;

import org.hamcrest.junit.MatcherAssert;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Subscriber;
import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.addmeal.model.MealIngredientsListModel.ParcelableProxy.CREATOR;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class MealIngredientsListModelProxyTest {
    final IngredientTemplate[] exampleIngredientTemplate = new IngredientTemplate[]{
            new IngredientTemplate(1L, "Ingredient 1", Uri.parse("http://example.com"),
                    DateTime.now(), AmountUnitType.MASS, new BigDecimal("329.7")),
            new IngredientTemplate(2L, "Ingredient 2", Uri.parse("http://example.com"),
                    DateTime.now(), AmountUnitType.VOLUME, new BigDecimal("42.0")),
            new IngredientTemplate(3L, "Ingredient 3", Uri.EMPTY,
                    DateTime.now(), AmountUnitType.VOLUME, new BigDecimal("60")),
    };
    final BigDecimal[] amounts = new BigDecimal[]{
            new BigDecimal("32.5"),
            new BigDecimal("42.50"),
            new BigDecimal("52.0"),
    };

    private RxIngredientsDatabaseModel ingredientTypesModel;
    private MealDatabaseModel mealDatabaseModel;
    private MealIngredientsListModel model;

    @Before
    public void setUp() throws Exception {
        Timber.plant(TimberUtils.ABOVE_WARN);
        TestRxPlugins.registerImmediateHookIO();
        ingredientTypesModel = Mockito.mock(RxIngredientsDatabaseModel.class);
        mealDatabaseModel = Mockito.mock(MealDatabaseModel.class);
        model = new MealIngredientsListModel(ingredientTypesModel, mealDatabaseModel, null, null);

        for (int i = 0; i < exampleIngredientTemplate.length; i++) {
            IngredientTypeParcel typeParcel = new IngredientTypeParcel(exampleIngredientTemplate[i]);
            when(ingredientTypesModel.unParcel(typeParcel))
                    .thenReturn(Observable.just(exampleIngredientTemplate[i]));
            when(ingredientTypesModel.getById(exampleIngredientTemplate[i].getId())).thenReturn(Observable.just(exampleIngredientTemplate[i]));
            model.addIngredientOfType(typeParcel, new BigDecimal(3.2 + i)).subscribe();
            model.getItemAt(i).setAmount(amounts[i]);
        }
        assertThat(model.getItemsCount(), equalTo(3));
    }

    @After
    public void tearDown() throws Exception {
        TestRxPlugins.reset();
        Timber.uprootAll();
    }

    @Test
    public void testOnSavedState() throws Exception {
        Bundle bundle = new Bundle();

        model.onSaveState(bundle);

        MealIngredientsListModel restoredModel = new MealIngredientsListModel(ingredientTypesModel, mealDatabaseModel, null, bundle);

        Assert.assertThat(restoredModel.ingredients, hasSize(exampleIngredientTemplate.length));
        for (int i = 0; i < exampleIngredientTemplate.length; i++) {
            Ingredient ing = restoredModel.getItemAt(i);
            assertThat(ing.getIngredientType(), equalTo(exampleIngredientTemplate[i]));
            assertThat(ing.getAmount(), equalTo(amounts[i]));
        }
    }

    @Test
    public void testRestoreFromParcel() throws Exception {
        Parcel parcel = Parcel.obtain();
        model.parcelableProxy.snapshot(model).writeToParcel(parcel, 0);

        parcel.setDataPosition(0);
        MealIngredientsListModel.ParcelableProxy fromParcel = CREATOR.createFromParcel(parcel);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MealIngredientsListModel.ParcelableProxy.STATE_MODEL, fromParcel);

        MealIngredientsListModel restoredModel = new MealIngredientsListModel(ingredientTypesModel, mealDatabaseModel, null, bundle);

        final AtomicBoolean isCompleted = new AtomicBoolean(false);
        final List<Integer> integers = new ArrayList<>(5);
        restoredModel.getItemsLoadedObservable().subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                isCompleted.set(true);
            }

            @Override
            public void onError(Throwable e) {
                throw new Error(e);
            }

            @Override
            public void onNext(Integer integer) {
                integers.add(integer);
            }
        });
        assertThat(isCompleted.get(), equalTo(true));
        assertThat(integers, hasItems(0, 1, 2));

        Assert.assertThat(restoredModel.ingredients, hasSize(exampleIngredientTemplate.length));
        for (int i = 0; i < exampleIngredientTemplate.length; i++) {
            Ingredient ing = restoredModel.getItemAt(i);
            assertThat(ing.getIngredientType(), equalTo(exampleIngredientTemplate[i]));
            assertThat(ing.getAmount(), equalTo(amounts[i]));
        }
        parcel.recycle();
    }

    @Test
    public void testSnapshot() throws Exception {
        model.parcelableProxy.snapshot(model);

        Assert.assertThat(model.parcelableProxy.ingredients, hasSize(exampleIngredientTemplate.length));
        for (int i = 0; i < exampleIngredientTemplate.length; i++) {
            Ingredient ing = model.parcelableProxy.ingredients.get(i);
            assertThat(ing.getIngredientType(), equalTo(exampleIngredientTemplate[i]));
            assertThat(ing.getAmount(), equalTo(amounts[i]));
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testModifyItemsException() throws Exception {
        model.modifyIngredient(-5, null, null);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testRemoveIngredientException() throws Exception {
        model.removeIngredient(-4);
    }

    @Test
    public void testProxyOther() throws Exception {
        assertThat(model.parcelableProxy.describeContents(), equalTo(0));
        MatcherAssert.assertThat(CREATOR.newArray(4), allOf(instanceOf(MealIngredientsListModel.ParcelableProxy[].class), arrayWithSize(4)));
    }

    @Test
    public void testLoadingError() throws Exception {
        Timber.uprootAll();

        final AtomicReference<Throwable> error = new AtomicReference<>();
        model.loadItems(Collections.singletonList(new Ingredient())).subscribe(new SimpleSubscriber<Integer>() {
            @Override
            public void onError(Throwable e) {
                error.set(e);
            }
        });
        assertThat(error.get(), instanceOf(NullPointerException.class));
    }
}
