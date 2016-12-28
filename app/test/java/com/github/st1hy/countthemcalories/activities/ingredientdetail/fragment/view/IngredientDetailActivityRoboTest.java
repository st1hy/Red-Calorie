package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view;


import android.content.Intent;
import android.os.Bundle;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewActivityRoboTest;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.github.st1hy.countthemcalories.testutils.TimberUtils;

import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.math.BigDecimal;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_TEMPLATE_PARCEL;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class IngredientDetailActivityRoboTest {
    final IngredientTemplate ingredientTemplate = IngredientsActivityRoboTest.exampleIngredients[0];
    final BigDecimal amount = new BigDecimal("24.65");
    final long requestId = -1L;

    private IngredientDetailActivity activity;
    private IngredientDetailFragment fragment;

    @Before
    public void setup() {
        Timber.plant(TimberUtils.ABOVE_WARN);
        TestRxPlugins.registerImmediateHookIO();
        DaoSession session = OverviewActivityRoboTest.prepareDatabase();
        IngredientsActivityRoboTest.addExampleIngredientsTagsAndJoin(session);
    }

    @After
    public void tearDown() throws Exception {
        Timber.uprootAll();
        TestRxPlugins.reset();
    }

    @Test(expected = NullPointerException.class)
    public void testIncorrectIntent() throws Exception {
        Timber.uprootAll(); //Don't print expected error
        activity = Robolectric.setupActivity(IngredientDetailActivity.class);
    }

    private void setupActivity() {
        activity = Robolectric.buildActivity(IngredientDetailActivity.class)
                .withIntent(buildIntent())
                .setup().get();
        assertThat(shadowOf(activity).isFinishing(), equalTo(false));
        setupFragment();
    }

    private void setupFragment() {
        fragment = (IngredientDetailFragment) activity.getSupportFragmentManager()
                .findFragmentByTag("ingredient detail content");
    }

    private Intent buildIntent() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL, amount.toPlainString());
        intent.putExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL, new IngredientTypeParcel(ingredientTemplate));
        intent.putExtra(EXTRA_INGREDIENT_ID_LONG, requestId);
        return intent;
    }

    @Test
    public void testDisplaysValues() throws Exception {
        setupActivity();
        assertThat(fragment.name.getText().toString(), equalTo(ingredientTemplate.getName()));
        assertThat(fragment.editAmount.getText().toString(), equalTo(amount.toPlainString()));
        assertThat(fragment.energyDensity.getText().toString(), equalTo("489.96 kcal / 100 g")); //20,5 [kJ] / 4,184 [kcal/kJ] * 100 [100g/g]
        assertThat(fragment.unit.getText().toString(), equalTo("g"));
        assertThat(fragment.calorieCount.getText().toString(), equalTo("120.78 kcal"));
    }

    @Test
    public void testOnStop() throws Exception {
        setupActivity();
        fragment.presenter = Mockito.mock(IngredientDetailPresenter.class);
        fragment.onStop();
        verify(fragment.presenter).onStop();
    }

    @Test
    public void testOnSaveState() throws Exception {
        setupActivity();
        fragment.presenter = Mockito.mock(IngredientDetailPresenter.class);
        fragment.onSaveInstanceState(new Bundle());
        verify(fragment.presenter).onSaveState(any(Bundle.class));
    }

    @Test
    public void testDisplayError() throws Exception {
        setupActivity();
        fragment.editAmount.setText("0");
        assertThat(fragment.editAmount.getError().toString(), equalTo(activity.getString(R.string.add_meal_amount_error_zero)));
    }

    @Test
    public void testRemove() throws Exception {
        setupActivity();
        fragment.remove.performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        assertThat(shadowActivity.isFinishing(), equalTo(true));
        assertThat(shadowActivity.getResultCode(), equalTo(IngredientDetailActivity.RESULT_REMOVE));
    }

    @Test
    public void testEditAndSave() throws Exception {
        setupActivity();
        final String expectedAmount = "34.7232223123";
        fragment.editAmount.setText(expectedAmount);
        fragment.accept.performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        assertThat(shadowActivity.isFinishing(), equalTo(true));
        assertThat(shadowActivity.getResultCode(), equalTo(IngredientDetailActivity.RESULT_OK));
        Intent resultIntent = shadowActivity.getResultIntent();
        IngredientTypeParcel parcel = resultIntent.getParcelableExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL);
        assertThat(parcel.getWhenReady().getOrNull(), equalTo(ingredientTemplate));
        assertThat(resultIntent.getLongExtra(EXTRA_INGREDIENT_ID_LONG, -124321L), equalTo(requestId));
        assertThat(resultIntent.getStringExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL), equalTo(expectedAmount));
    }

    @Test
    public void testDisplayErrorOnAccept() throws Exception {
        Intent intent = buildIntent();
        intent.putExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL, "0");

        activity = Robolectric.buildActivity(IngredientDetailActivity.class)
                .withIntent(intent)
                .setup().get();
        setupFragment();

        assertThat(fragment.editAmount.getError(), nullValue());
        fragment.accept.performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        assertThat(shadowActivity.isFinishing(), equalTo(false));
        assertThat(fragment.editAmount.getError(), IsEqual.<CharSequence>equalTo(activity.getString(R.string.add_meal_amount_error_empty)));
    }

}