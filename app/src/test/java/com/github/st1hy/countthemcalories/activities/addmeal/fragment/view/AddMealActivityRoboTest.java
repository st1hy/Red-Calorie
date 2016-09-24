package com.github.st1hy.countthemcalories.activities.addmeal.fragment.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.view.TestAddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.github.st1hy.countthemcalories.testutils.TimberUtils;
import com.google.common.base.Preconditions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.shadows.ShadowActivity;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsActivityRoboTest.exampleIngredients;
import static com.github.st1hy.countthemcalories.core.baseview.BaseActivity.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class AddMealActivityRoboTest {

    private TestAddMealActivity activity;
    private AddMealFragment fragment;

    @Before
    public void setup() {
        Timber.plant(TimberUtils.ABOVE_WARN);
        TestRxPlugins.registerImmediateHookIO();
        DaoSession daoSession = OverviewActivityRoboTest.prepareDatabase();
        IngredientsActivityRoboTest.addExampleIngredientsTagsAndJoin(daoSession);
        activity = Robolectric.setupActivity(TestAddMealActivity.class);
        fragment = (AddMealFragment) activity.getSupportFragmentManager()
                .findFragmentByTag("add meal content");
    }

    @After
    public void tearDown() throws Exception {
        Timber.uprootAll();
        TestRxPlugins.reset();
    }

    @Test
    public void testSaveButtonAction() throws Exception {
        ShadowActivity shadowActivity = shadowOf(activity);
        shadowActivity.onCreateOptionsMenu(new RoboMenu(activity));
        fragment.name.setText("Name");
        addIngredient();
        handleNewIngredient();
        shadowActivity.clickMenuItem(R.id.action_save);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity, hasComponent(new ComponentName(activity, OverviewActivity.class)));
    }

    @Test
    public void testSaveErrors() throws Exception {
        ShadowActivity shadowActivity = shadowOf(activity);
        shadowActivity.onCreateOptionsMenu(new RoboMenu(activity));
        shadowActivity.clickMenuItem(R.id.action_save);
        assertThat(shadowActivity.isFinishing(), equalTo(false));
        assertThat(fragment.name.getError(), notNullValue());
        assertNotNull(activity.ingredientsError());
        assertThat(activity.ingredientsError().isShownOrQueued(), equalTo(true));
    }

    @Test
    public void testOnSaveInstance() throws Exception {
        fragment.presenter = mock(AddMealPresenter.class);
        fragment.onSaveInstanceState(new Bundle());
        verify(fragment.presenter).onSaveState(any(Bundle.class));
    }

    @Test
    public void testAddIngredient() throws Exception {
        assertThat(fragment.ingredientList.getChildCount(), equalTo(0));

        addIngredient();
        handleNewIngredient();

        assertThat(fragment.ingredientList.getChildCount(), equalTo(1));
    }

    @Test
    public void testAddIngredient2() throws Exception {
        assertThat(fragment.ingredientList.getChildCount(), equalTo(0));

        addIngredientFromParcel();
        handleNewIngredient();

        assertThat(fragment.ingredientList.getChildCount(), equalTo(1));
    }

    private void addIngredient() {
        Preconditions.checkNotNull(activity.findViewById(R.id.add_meal_fab_add_ingredient)).performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.getNextStartedActivity();
        assertThat(requestIntent, hasComponent(new ComponentName(activity, IngredientsActivity.class)));
        assertThat(requestIntent, hasAction(IngredientsActivity.ACTION_SELECT_INGREDIENT));

        Intent intent = new Intent();
        intent.putExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL, new IngredientTypeParcel(exampleIngredients[0]));
        shadowActivity.receiveResult(requestIntent, Activity.RESULT_OK, intent);
    }


    private void addIngredientFromParcel() {
        Preconditions.checkNotNull(activity.findViewById(R.id.add_meal_fab_add_ingredient)).performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.getNextStartedActivity();
        assertThat(requestIntent, hasComponent(new ComponentName(activity, IngredientsActivity.class)));
        assertThat(requestIntent, hasAction(IngredientsActivity.ACTION_SELECT_INGREDIENT));

        Intent intent = new Intent();
        IngredientTypeParcel typeParcel = new IngredientTypeParcel(exampleIngredients[0]);
        Parcel parcel = Parcel.obtain();
        typeParcel.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        IngredientTypeParcel fromParcel = IngredientTypeParcel.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        intent.putExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL, fromParcel);
        shadowActivity.receiveResult(requestIntent, Activity.RESULT_OK, intent);
    }

    private void handleNewIngredient() {
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.getNextStartedActivity();
        assertThat(requestIntent, hasComponent(new ComponentName(activity, IngredientDetailActivity.class)));
        assertThat(requestIntent, hasExtraWithKey(IngredientsDetailFragmentModule.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL));
        assertThat(requestIntent, hasExtraWithKey(IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG));
        assertThat(requestIntent, hasExtraWithKey(IngredientsDetailFragmentModule.EXTRA_INGREDIENT_TEMPLATE_PARCEL));

        shadowActivity.receiveResult(requestIntent, Activity.RESULT_OK, requestIntent);
    }


    @Test
    public void testEditIngredient() throws Exception {
        testAddIngredient();

        checkFirstIngredientAmountEqualTo("0 g");

        fragment.ingredientList.getChildAt(0).findViewById(R.id.add_meal_ingredient_compact).performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.getNextStartedActivity();
        Intent result = new Intent(requestIntent);
        result.putExtra(IngredientsDetailFragmentModule.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL, "12.5");

        shadowActivity.receiveResult(requestIntent, Activity.RESULT_OK, result);

        checkFirstIngredientAmountEqualTo("12.5 g");
    }

    private void checkFirstIngredientAmountEqualTo(String amountString) {
        View childRoot = fragment.ingredientList.getChildAt(0).findViewById(R.id.add_meal_ingredient_root);
        assertThat(childRoot, notNullValue());
        TextView amount = (TextView) childRoot.findViewById(R.id.add_meal_ingredient_amount);
        assertThat(amount.getText().toString(), equalTo(amountString));
    }

    @Test
    public void testRemoveIngredient() throws Exception {
        testAddIngredient();

        assertThat(fragment.ingredientList.getChildCount(), equalTo(1));

        fragment.ingredientList.getChildAt(0).findViewById(R.id.add_meal_ingredient_compact).performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.getNextStartedActivity();

        shadowActivity.receiveResult(requestIntent, IngredientDetailActivity.RESULT_REMOVE, requestIntent);

        assertThat(fragment.ingredientList.getChildCount(), equalTo(0));
    }
}
