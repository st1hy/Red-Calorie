package com.github.st1hy.countthemcalories.activities.mealdetail.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.matcher.IntentMatchers;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.mealdetail.presenter.MealDetailPresenter;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivityRoboTest;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class MealDetailActivityRoboTest {
    private final Meal meal = OverviewActivityRoboTest.exampleMeals[0];

    private final Timber.Tree tree = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(message);
        }
    };

    MealDetailActivity activity;


    @Before
    public void setUp() throws Exception {
        Timber.plant(tree);
        TestRxPlugins.registerImmediateHookIO();
        DaoSession session = OverviewActivityRoboTest.prepareDatabase();
        OverviewActivityRoboTest.addMealsIngredients(session);

    }

    @After
    public void tearDown() throws Exception {
        Timber.uproot(tree);
        TestRxPlugins.reset();
    }

    @Test
    public void testIncorrectIntent() throws Exception {
        MealDetailActivity activity = Robolectric.setupActivity(MealDetailActivity.class);
        ShadowActivity shadowActivity = shadowOf(activity);
        assertThat(shadowActivity.isFinishing(), equalTo(true));
        assertThat(shadowActivity.getResultCode(), equalTo(Activity.RESULT_CANCELED));
    }


    private void setupActivity() {
        activity = Robolectric.buildActivity(MealDetailActivity.class)
                .withIntent(buildIntent())
                .setup().get();
        assertThat(shadowOf(activity).isFinishing(), equalTo(false));
    }

    private Intent buildIntent() {
        Intent intent = new Intent();
        intent.putExtra(MealDetailActivity.EXTRA_MEAL_PARCEL, new MealParcel(meal));
        return intent;
    }

    @Test
    public void testDisplaysValues() throws Exception {
        setupActivity();
        assertThat(activity.name.getText().toString(), equalTo(meal.getName()));
        assertThat(activity.energy.getText().toString(), equalTo("175.86 kcal")); //30 [g] * 20.5 [kJ/g]  + 20 [g] * 6.04 [kJ/g] converted to kcal / 100 g
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(2));
    }

    @Test
    public void testSaveState() throws Exception {
        setupActivity();
        Bundle bundle = new Bundle();
        activity.onSaveInstanceState(bundle);
        assertThat(bundle.isEmpty(), equalTo(false));
    }

    @Test
    public void testEdit() throws Exception {
        setupActivity();
        activity.editButton.performClick();
        assertThat(shadowOf(activity).isFinishing(), equalTo(true));
        assertThat(shadowOf(activity).getResultCode(), equalTo(MealDetailActivity.RESULT_EDIT));
        assertThat(shadowOf(activity).getResultIntent(), IntentMatchers.hasExtra(MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG, meal.getId()));
    }

    @Test
    public void testRemove() throws Exception {
        setupActivity();
        activity.removeButton.performClick();
        assertThat(shadowOf(activity).isFinishing(), equalTo(true));
        assertThat(shadowOf(activity).getResultCode(), equalTo(MealDetailActivity.RESULT_DELETE));
        assertThat(shadowOf(activity).getResultIntent(), IntentMatchers.hasExtra(MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG, meal.getId()));
    }

    @Test
    public void testStop() throws Exception {
        setupActivity();
        activity.presenter = Mockito.mock(MealDetailPresenter.class);
        activity.onStop();
        verify(activity.presenter).onStop();
    }
}