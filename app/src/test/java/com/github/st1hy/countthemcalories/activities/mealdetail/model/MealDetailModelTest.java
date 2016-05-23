package com.github.st1hy.countthemcalories.activities.mealdetail.model;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDatabaseModel;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.github.st1hy.countthemcalories.testutils.SimpleSubscriber;
import com.github.st1hy.countthemcalories.testutils.TestError;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.functions.Action1;
import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class MealDetailModelTest {
    public static final Meal example = new Meal(1L, "Meal 1", Uri.EMPTY, DateTime.now());

    final Timber.Tree tree = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(message);
        }
    };
    private MealDatabaseModel mealDatabaseModel;
    private MealDetailModel model;

    @Before
    public void setUp() throws Exception {
        Timber.plant(tree);
        TestRxPlugins.registerImmediateHookIO();
        mealDatabaseModel = Mockito.mock(MealDatabaseModel.class);
        when(mealDatabaseModel.unParcel(any(MealParcel.class)))
                .thenReturn(Observable.just(example));
        Intent intent = new Intent();
        intent.putExtra(MealDetailActivity.EXTRA_MEAL_PARCEL, new MealParcel(example));
        model = new MealDetailModel(mealDatabaseModel, intent, null);
        assertThat(model.isDataValid(), equalTo(true));
    }

    @After
    public void tearDown() throws Exception {
        TestRxPlugins.reset();
        Timber.uprootAll();
    }

    @Test
    public void testGetMealObservable() throws Exception {
        final AtomicReference<Meal> loadedMeal = new AtomicReference<>();
        model.getMealObservable().subscribe(new Action1<Meal>() {
            @Override
            public void call(Meal meal) {
                loadedMeal.set(meal);
            }
        });
        Meal meal = loadedMeal.get();
        assertThat(meal, equalTo(example));
    }

    @Test
    public void testInvalidIntent() throws Exception {
        model = new MealDetailModel(mealDatabaseModel, null, null);
        assertThat(model.isDataValid(), equalTo(false));
        final AtomicReference<List<Meal>> list = new AtomicReference<>();
        model.getMealObservable().toList().subscribe(new Action1<List<Meal>>() {
            @Override
            public void call(List<Meal> meals) {
                list.set(meals);
            }
        });
        assertThat(list.get(), hasSize(0));
    }

    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = new Bundle();

        model.onSaveState(bundle);

        MealDetailModel restoredModel = new MealDetailModel(mealDatabaseModel, null, bundle);
        checkRestoredModel(restoredModel);
    }

    @Test
    public void testRestoreFromParcel() throws Exception {
        Parcel parcel = Parcel.obtain();
        model.parcelableProxy.snapshot(model).writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        MealDetailModel.ParcelableProxy fromParcel = MealDetailModel.ParcelableProxy.CREATOR.createFromParcel(parcel);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MealDetailModel.ParcelableProxy.STATE_MODEL, fromParcel);

        MealDetailModel restoredModel = new MealDetailModel(mealDatabaseModel, null, bundle);
        checkRestoredModel(restoredModel);
        parcel.recycle();
    }

    private void checkRestoredModel(MealDetailModel restoredModel) {
        assertThat(restoredModel.isDataValid(), equalTo(true));
        Meal meal = restoredModel.getMeal();
        assertThat(meal, equalTo(example));

        final AtomicReference<List<Meal>> list = new AtomicReference<>();
        restoredModel.getMealObservable().toList().subscribe(new Action1<List<Meal>>() {
            @Override
            public void call(List<Meal> meals) {
                list.set(meals);
            }
        });
        assertThat(list.get(), hasSize(1));
        assertThat(list.get(), hasItems(meal));
    }

    @Test
    public void testLoadingError() throws Exception {
        Timber.uprootAll();

        Meal meal = new Meal();
        meal.setId(-1L);
        Parcel parcel = Parcel.obtain();
        new MealParcel(meal).writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        MealParcel fromParcel = MealParcel.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        when(mealDatabaseModel.unParcel(fromParcel)).thenReturn(Observable.<Meal>error(new TestError()));
        final AtomicReference<Throwable> error = new AtomicReference<>();
        model.loadFromParcel(fromParcel).subscribe(new SimpleSubscriber<Meal>() {
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
        assertThat(MealDetailModel.ParcelableProxy.CREATOR.newArray(4),
                allOf(instanceOf(MealDetailModel.ParcelableProxy[].class), arrayWithSize(4)));
    }

    @Test
    public void testLoadParcelFromIntent() throws Exception {
        Parcel parcel = Parcel.obtain();
        new MealParcel(example).writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        MealParcel fromParcel = MealParcel.CREATOR.createFromParcel(parcel);
        parcel.recycle();

        Intent intent = new Intent();
        intent.putExtra(MealDetailActivity.EXTRA_MEAL_PARCEL, fromParcel);

        model = new MealDetailModel(mealDatabaseModel, intent, null);
        assertThat(model.isDataValid(), equalTo(true));
        verify(mealDatabaseModel).unParcel(fromParcel);
        testGetMealObservable();
    }
}