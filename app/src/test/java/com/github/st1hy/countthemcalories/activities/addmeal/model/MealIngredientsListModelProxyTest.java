package com.github.st1hy.countthemcalories.activities.addmeal.model;


import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;

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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscriber;
import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.addmeal.model.MealIngredientsListModel.ParcelableProxy.CREATOR;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_ML;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KJ_AT_100ML;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MealIngredientsListModelProxyTest {
    final IngredientTemplate[] exampleIngredientTemplate = new IngredientTemplate[]{
            new IngredientTemplate(1L, "Ingredient 1", Uri.parse("http://example.com"),
                    DateTime.now(), EnergyDensityUtils.getOrZero(KCAL_AT_100G, "329.7")),
            new IngredientTemplate(2L, "Ingredient 2", Uri.parse("http://example.com"),
                    DateTime.now(), EnergyDensityUtils.getOrZero(KCAL_AT_ML, "42.0")),
            new IngredientTemplate(3L, "Ingredient 3", Uri.EMPTY,
                    DateTime.now(), EnergyDensityUtils.getOrZero(KJ_AT_100ML, "60")),
    };
    final BigDecimal[] amounts = new BigDecimal[] {
            new BigDecimal("32.5"),
            new BigDecimal("42.50"),
            new BigDecimal("52.0"),
    };
    final Timber.Tree tree = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(message);
        }
    };

    private IngredientTypesModel ingredientTypesModel;
    private MealIngredientsListModel model;

    @Before
    public void setUp() throws Exception {
        Timber.plant(tree);
        TestRxPlugins.registerImmediateHookIO();
        ingredientTypesModel = Mockito.mock(IngredientTypesModel.class);
        model = new MealIngredientsListModel(ingredientTypesModel, null);

        for (int i = 0; i < exampleIngredientTemplate.length; i++) {
            long id = exampleIngredientTemplate[i].getId();
            when(ingredientTypesModel.getById(id))
                    .thenReturn(Observable.just(exampleIngredientTemplate[i]));
            model.addIngredientOfType(id).subscribe();
            model.getItemAt(i).setAmount(amounts[i]);
        }
        assertThat(model.getItemsCount(), equalTo(3));
    }

    @After
    public void tearDown() throws Exception {
        TestRxPlugins.reset();
        Timber.uproot(tree);
    }

    @Test
    public void testOnSavedState() throws Exception {
        Bundle bundle = new Bundle();

        model.onSaveState(bundle);

        MealIngredientsListModel restoredModel = new MealIngredientsListModel(ingredientTypesModel, bundle);

        Assert.assertThat(restoredModel.ingredients, hasSize(exampleIngredientTemplate.length));
        for (int i  = 0; i < exampleIngredientTemplate.length; i++) {
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

        MealIngredientsListModel restoredModel = new MealIngredientsListModel(ingredientTypesModel, bundle);

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
        for (int i  = 0; i < exampleIngredientTemplate.length; i++) {
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
        for (int i  = 0; i < exampleIngredientTemplate.length; i++) {
            Ingredient ing = model.parcelableProxy.ingredients.get(i);
            assertThat(ing.getIngredientType(), equalTo(exampleIngredientTemplate[i]));
            assertThat(ing.getAmount(), equalTo(amounts[i]));
        }
    }
}
