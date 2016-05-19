package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivityRoboTest;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.shadows.ShadowActivity;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivityRoboTest.exampleIngredients;
import static com.github.st1hy.countthemcalories.core.baseview.BaseActivity.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class AddMealActivityRoboTest {
    private AddMealActivity activity;
    private final Timber.Tree tree = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(message);
        }
    };

    @Before
    public void setup() {
        Timber.plant(tree);
        TestRxPlugins.registerImmediateHookIO();
        DaoSession daoSession = OverviewActivityRoboTest.prepareDatabase();
        IngredientsActivityRoboTest.addExampleIngredientsTagsAndJoin(daoSession);
        activity = Robolectric.setupActivity(AddMealActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        Timber.uproot(tree);
        TestRxPlugins.reset();
    }

    @Test
    public void testSaveButtonAction() throws Exception {
        ShadowActivity shadowActivity = shadowOf(activity);
        shadowActivity.onCreateOptionsMenu(new RoboMenu());
        activity.name.setText("Name");
        addIngredient();
        handleNewIngredient();
        shadowActivity.clickMenuItem(R.id.action_save);
        assertThat(shadowActivity.isFinishing(), equalTo(true));
        assertThat(shadowActivity.getResultCode(), equalTo(Activity.RESULT_OK));
    }


    @Test
    public void testSaveErrors() throws Exception {
        ShadowActivity shadowActivity = shadowOf(activity);
        shadowActivity.onCreateOptionsMenu(new RoboMenu());
        shadowActivity.clickMenuItem(R.id.action_save);
        assertThat(shadowActivity.isFinishing(), equalTo(false));
        assertThat(activity.name.getError(), notNullValue());
        assertNotNull(activity.ingredientsError);
        assertThat(activity.ingredientsError.isShownOrQueued(), equalTo(true));
    }

    @Test
    public void testOnSaveInstance() throws Exception {
        activity.presenter = mock(AddMealPresenter.class);
        activity.onSaveInstanceState(new Bundle());
        verify(activity.presenter).onSaveState(any(Bundle.class));
    }

    @Test
    public void testAddIngredient() throws Exception {
        assertThat(activity.ingredientList.getChildCount(), equalTo(0));

        addIngredient();
        handleNewIngredient();

        assertThat(activity.ingredientList.getChildCount(), equalTo(1));
    }

    @Test
    public void testAddIngredient2() throws Exception {
        assertThat(activity.ingredientList.getChildCount(), equalTo(0));

        addIngredientFromParcel();
        handleNewIngredient();

        assertThat(activity.ingredientList.getChildCount(), equalTo(1));
    }



    private void addIngredient() {
        activity.addIngredientFab.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.getNextStartedActivity();
        assertThat(requestIntent, hasComponent(new ComponentName(activity, IngredientsActivity.class)));
        assertThat(requestIntent, hasAction(IngredientsActivity.ACTION_SELECT_INGREDIENT));

        Intent intent = new Intent();
        intent.putExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL, new IngredientTypeParcel(exampleIngredients[0]));
        shadowActivity.receiveResult(requestIntent, Activity.RESULT_OK, intent);
    }


    private void addIngredientFromParcel() {
        activity.addIngredientFab.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.getNextStartedActivity();
        assertThat(requestIntent, hasComponent(new ComponentName(activity, IngredientsActivity.class)));
        assertThat(requestIntent, hasAction(IngredientsActivity.ACTION_SELECT_INGREDIENT));

        Intent intent = new Intent();
        IngredientTypeParcel typeParcel = new IngredientTypeParcel(exampleIngredients[0]);
        Parcel parcel = Parcel.obtain();
        typeParcel.writeToParcel(parcel,0);
        parcel.setDataPosition(0);
        IngredientTypeParcel fromParcel = IngredientTypeParcel.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        intent.putExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL, fromParcel);
        shadowActivity.receiveResult(requestIntent, Activity.RESULT_OK, intent);
    }

    private void handleNewIngredient() {
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.getNextStartedActivity();
        assertThat(requestIntent, hasComponent(new ComponentName(activity, IngredientDetailsActivity.class)));
        assertThat(requestIntent, hasAction(IngredientDetailsActivity.ACTION_EDIT_INGREDIENT));
        assertThat(requestIntent, hasExtraWithKey(IngredientDetailsActivity.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL));
        assertThat(requestIntent, hasExtraWithKey(IngredientDetailsActivity.EXTRA_INGREDIENT_ID_LONG));
        assertThat(requestIntent, hasExtraWithKey(IngredientDetailsActivity.EXTRA_INGREDIENT_TEMPLATE_PARCEL));

        shadowActivity.receiveResult(requestIntent, Activity.RESULT_OK, requestIntent);
    }


    @Test
    public void testEditIngredient() throws Exception {
        testAddIngredient();

        checkFirstIngredientAmountEqualTo("0 g");

        activity.ingredientList.getChildAt(0).findViewById(R.id.add_meal_ingredient_compact).performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.getNextStartedActivity();
        Intent result = new Intent(requestIntent);
        result.putExtra(IngredientDetailsActivity.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL, "12.5");

        shadowActivity.receiveResult(requestIntent, Activity.RESULT_OK, result);

        checkFirstIngredientAmountEqualTo("12.5 g");
    }

    private void checkFirstIngredientAmountEqualTo(String amountString) {
        View childRoot = activity.ingredientList.getChildAt(0).findViewById(R.id.add_meal_ingredient_root);
        assertThat(childRoot, notNullValue());
        TextView amount = (TextView) childRoot.findViewById(R.id.add_meal_ingredient_amount);
        assertThat(amount.getText().toString(), equalTo(amountString));
    }

    @Test
    public void testRemoveIngredient() throws Exception {
        testAddIngredient();

        assertThat(activity.ingredientList.getChildCount(), equalTo(1));

        activity.ingredientList.getChildAt(0).findViewById(R.id.add_meal_ingredient_compact).performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.getNextStartedActivity();

        shadowActivity.receiveResult(requestIntent, IngredientDetailsActivity.RESULT_REMOVE, requestIntent);

        assertThat(activity.ingredientList.getChildCount(), equalTo(0));
    }
}
