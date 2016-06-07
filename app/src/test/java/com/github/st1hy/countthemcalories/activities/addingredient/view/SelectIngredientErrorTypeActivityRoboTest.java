package com.github.st1hy.countthemcalories.activities.addingredient.view;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class SelectIngredientErrorTypeActivityRoboTest {

    SelectIngredientTypeActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(SelectIngredientTypeActivity.class);
    }

    @Test
    public void testSelectMeal() throws Exception {
        activity.meal.performClick();

        assertThat(shadowOf(activity).isFinishing(), equalTo(true));
        assertThat(shadowOf(activity).getResultCode(), equalTo(SelectIngredientTypeActivity.RESULT_MEAL));
    }

    @Test
    public void testSelectDrink() throws Exception {
        activity.drink.performClick();

        assertThat(shadowOf(activity).isFinishing(), equalTo(true));
        assertThat(shadowOf(activity).getResultCode(), equalTo(SelectIngredientTypeActivity.RESULT_DRINK));
    }
}