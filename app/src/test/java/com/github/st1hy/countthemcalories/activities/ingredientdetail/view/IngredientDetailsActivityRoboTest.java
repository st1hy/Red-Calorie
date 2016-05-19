package com.github.st1hy.countthemcalories.activities.ingredientdetail.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivityRoboTest;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.math.BigDecimal;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity.EXTRA_INGREDIENT_ID_LONG;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity.EXTRA_INGREDIENT_TEMPLATE_PARCEL;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class IngredientDetailsActivityRoboTest {
    final IngredientTemplate ingredientTemplate = IngredientsActivityRoboTest.exampleIngredients[0];
    final BigDecimal amount = new BigDecimal("24.65");
    final long requestId = -1L;

    private final Timber.Tree tree = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(message);
        }
    };


    private IngredientDetailsActivity activity;

    @Before
    public void setup() {
        Timber.plant(tree);
        TestRxPlugins.registerImmediateHookIO();
        DaoSession session = OverviewActivityRoboTest.prepareDatabase();
        IngredientsActivityRoboTest.addExampleIngredientsTagsAndJoin(session);
    }

    @After
    public void tearDown() throws Exception {
        Timber.uproot(tree);
        TestRxPlugins.reset();
    }

    @Test
    public void testIncorrectIntent() throws Exception {
        activity = Robolectric.setupActivity(IngredientDetailsActivity.class);
        ShadowActivity shadowActivity = shadowOf(activity);
        assertThat(shadowActivity.isFinishing(), equalTo(true));
        assertThat(shadowActivity.getResultCode(), equalTo(Activity.RESULT_CANCELED));
    }

    private void setupActivity() {
        activity = Robolectric.buildActivity(IngredientDetailsActivity.class)
                .withIntent(buildIntent())
                .setup().get();
        assertThat(shadowOf(activity).isFinishing(), equalTo(false));
    }

    private Intent buildIntent() {
        Intent intent = new Intent(IngredientDetailsActivity.ACTION_EDIT_INGREDIENT);
        intent.putExtra(IngredientDetailsActivity.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL, amount.toPlainString());
        intent.putExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL, new IngredientTypeParcel(ingredientTemplate));
        intent.putExtra(IngredientDetailsActivity.EXTRA_INGREDIENT_ID_LONG, requestId);
        return intent;
    }

    @Test
    public void testDisplaysValues() throws Exception {
        setupActivity();
        assertThat(activity.name.getText().toString(), equalTo(ingredientTemplate.getName()));
        assertThat(activity.editAmount.getText().toString(), equalTo(amount.toPlainString()));
        assertThat(activity.energyDensity.getText().toString(), equalTo("489.96 kcal / 100 g")); //20,5 [kJ] / 4,184 [kcal/kJ] * 100 [100g/g]
        assertThat(activity.unit.getText().toString(), equalTo("g"));
        assertThat(activity.calorieCount.getText().toString(), equalTo("120.78 kcal"));
    }

    @Test
    public void testOnStop() throws Exception {
        setupActivity();
        activity.presenter = Mockito.mock(IngredientDetailPresenter.class);
        activity.onStop();
        verify(activity.presenter).onStop();
    }

    @Test
    public void testOnSaveState() throws Exception {
        setupActivity();
        activity.presenter = Mockito.mock(IngredientDetailPresenter.class);
        activity.onSaveInstanceState(new Bundle());
        verify(activity.presenter).onSaveState(any(Bundle.class));
    }

    @Test
    public void testDisplayError() throws Exception {
        setupActivity();
        activity.editAmount.setText("0");
        assertThat(activity.editAmount.getError().toString(), equalTo(activity.getString(R.string.add_meal_amount_error_zero)));
    }

    @Test
    public void testRemove() throws Exception {
        setupActivity();
        activity.remove.performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        assertThat(shadowActivity.isFinishing(), equalTo(true));
        assertThat(shadowActivity.getResultCode(), equalTo(IngredientDetailsActivity.RESULT_REMOVE));
    }

    @Test
    public void testEditAndSave() throws Exception {
        setupActivity();
        final String expectedAmount = "34.7232223123";
        activity.editAmount.setText(expectedAmount);
        activity.accept.performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        assertThat(shadowActivity.isFinishing(), equalTo(true));
        assertThat(shadowActivity.getResultCode(), equalTo(IngredientDetailsActivity.RESULT_OK));
        Intent resultIntent = shadowActivity.getResultIntent();
        IngredientTypeParcel parcel = resultIntent.getParcelableExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL);
        assertThat(parcel.getWhenReady().getOrNull(), equalTo(ingredientTemplate));
        assertThat(resultIntent.getLongExtra(EXTRA_INGREDIENT_ID_LONG, -124321L),  equalTo(requestId));
        assertThat(resultIntent.getStringExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL), equalTo(expectedAmount));
    }

    @Test
    public void testDisplayErrorOnAccept() throws Exception {
        Intent intent = buildIntent();
        intent.putExtra(IngredientDetailsActivity.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL, "0");

        activity = Robolectric.buildActivity(IngredientDetailsActivity.class)
                .withIntent(intent)
                .setup().get();

        assertThat(activity.editAmount.getError(), nullValue());
        activity.accept.performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        assertThat(shadowActivity.isFinishing(), equalTo(false));
        assertThat(activity.editAmount.getError(), IsEqual.<CharSequence>equalTo(activity.getString(R.string.add_meal_amount_error_empty)));
    }

}