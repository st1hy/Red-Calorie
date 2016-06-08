package com.github.st1hy.countthemcalories.activities.ingredientdetail.model;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.github.st1hy.countthemcalories.testutils.SimpleSubscriber;
import com.github.st1hy.countthemcalories.testutils.TestError;
import com.github.st1hy.countthemcalories.testutils.TimberUtils;

import org.hamcrest.Matchers;
import org.hamcrest.junit.MatcherAssert;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.functions.Action1;
import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.model.IngredientDetailModel.ParcelableProxy.CREATOR;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity.ACTION_EDIT_INGREDIENT;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity.EXTRA_INGREDIENT_TEMPLATE_PARCEL;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class IngredientDetailModelTest {
    final IngredientTemplate example = new IngredientTemplate(1L, "Ingredient 1", Uri.parse("http://example.com"), DateTime.now(), AmountUnitType.MASS, EnergyDensityUtils.getOrZero("329.7"));

    final BigDecimal amount = new BigDecimal("32.5");

    private RxIngredientsDatabaseModel ingredientTypesModel;
    private Resources resources;
    private IngredientDetailModel model;

    @Before
    public void setUp() throws Exception {
        Timber.plant(TimberUtils.ABOVE_WARN);
        TestRxPlugins.registerImmediateHookIO();
        resources = RuntimeEnvironment.application.getResources();
        ingredientTypesModel = Mockito.mock(RxIngredientsDatabaseModel.class);
        when(ingredientTypesModel.unParcel(any(IngredientTypeParcel.class)))
                .thenReturn(Observable.just(example));
        when(ingredientTypesModel.getById(example.getId()))
                .thenReturn(Observable.just(example));
        Intent intent = new Intent();
        intent.setAction(ACTION_EDIT_INGREDIENT);
        intent.putExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL, new IngredientTypeParcel(example));
        intent.putExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL, amount.toPlainString());
        model = new IngredientDetailModel(ingredientTypesModel, resources, intent, null);
        assertThat(model.isDataValid(), equalTo(true));
    }

    @After
    public void tearDown() throws Exception {
        TestRxPlugins.reset();
        Timber.uprootAll();
    }

    @Test
    public void testGetIngredientObservable() throws Exception {
        final AtomicReference<Ingredient> loadedIngredient = new AtomicReference<>();
        model.getIngredientObservable().subscribe(new Action1<Ingredient>() {
            @Override
            public void call(Ingredient ingredient) {
                loadedIngredient.set(ingredient);
            }
        });
        Ingredient ingredient = loadedIngredient.get();
        assertThat(ingredient, notNullValue());
        assertThat(ingredient.getId(), equalTo(-1L));
        assertThat(ingredient.getIngredientType(), equalTo(example));
        assertThat(ingredient.getAmount(), equalTo(amount));
    }

    @Test
    public void testInvalidIntent() throws Exception {
        Timber.uprootAll(); //Don't print expected error
        model = new IngredientDetailModel(ingredientTypesModel, resources, null, null);
        assertThat(model.isDataValid(), equalTo(false));
        final AtomicReference<List<Ingredient>> list = new AtomicReference<>();
        model.getIngredientObservable().toList().subscribe(new Action1<List<Ingredient>>() {
            @Override
            public void call(List<Ingredient> ingredients) {
                list.set(ingredients);
            }
        });
        assertThat(list.get(), hasSize(0));
    }

    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = new Bundle();

        model.onSaveState(bundle);

        IngredientDetailModel restoredModel = new IngredientDetailModel(ingredientTypesModel, resources, null, bundle);
        checkRestoredModel(restoredModel);
    }

    @Test
    public void testRestoreFromParcel() throws Exception {
        Parcel parcel = Parcel.obtain();
        model.parcelableProxy.snapshot(model).writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        IngredientDetailModel.ParcelableProxy fromParcel = CREATOR.createFromParcel(parcel);
        Bundle bundle = new Bundle();
        bundle.putParcelable(IngredientDetailModel.ParcelableProxy.STATE_MODEL, fromParcel);

        IngredientDetailModel restoredModel = new IngredientDetailModel(ingredientTypesModel, resources, null, bundle);
        checkRestoredModel(restoredModel);

    }

    private void checkRestoredModel(IngredientDetailModel restoredModel) {
        assertThat(restoredModel.isDataValid(), equalTo(true));
        Ingredient ingredient = restoredModel.getIngredient();
        assertThat(ingredient, notNullValue());
        assertThat(ingredient.getAmount().toPlainString(), equalTo(amount.toPlainString()));
        assertThat(ingredient.getIngredientType(), equalTo(example));

        final AtomicReference<List<Ingredient>> list = new AtomicReference<>();
        restoredModel.getIngredientObservable().toList().subscribe(new Action1<List<Ingredient>>() {
            @Override
            public void call(List<Ingredient> ingredients) {
                list.set(ingredients);
            }
        });
        assertThat(list.get(), hasSize(1));
        assertThat(list.get(), hasItems(ingredient));
    }

    @Test
    public void testGetErrorString() throws Exception {
        assertThat(model.getErrorString("12.3"), nullValue());
        assertThat(model.getErrorString("0123214623363463"), nullValue());
        assertThat(model.getErrorString("0.00000"),
                equalTo(resources.getString(R.string.add_meal_amount_error_zero)));
        assertThat(model.getErrorString("0"),
                equalTo(resources.getString(R.string.add_meal_amount_error_zero)));
        assertThat(model.getErrorString(""),
                equalTo(resources.getString(R.string.add_meal_amount_error_empty)));
    }

    @Test
    public void testSetAmount() throws Exception {
        final String expected = "3214555.55555";
        model.setIngredientAmount(new BigDecimal(expected));

        assertThat(model.getIngredient().getAmount().toPlainString(),
                equalTo(expected));
    }

    @Test
    public void testLoadingError() throws Exception {
        Timber.uprootAll();

        when(ingredientTypesModel.getById(-1)).thenReturn(Observable.<IngredientTemplate>error(new TestError()));
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientTypeId(-1L);
        final AtomicReference<Throwable> error = new AtomicReference<>();
        model.loadItem(ingredient).subscribe(new SimpleSubscriber<Ingredient>() {
            @Override
            public void onError(Throwable e) {
                error.set(e);
            }
        });
        assertThat(error.get(), instanceOf(TestError.class));
    }

    @Test
    public void testProxyOther() throws Exception {
        assertThat(model.parcelableProxy.describeContents(), Matchers.equalTo(0));
        MatcherAssert.assertThat(IngredientDetailModel.ParcelableProxy.CREATOR.newArray(4),
                allOf(instanceOf(IngredientDetailModel.ParcelableProxy[].class), arrayWithSize(4)));
    }

}