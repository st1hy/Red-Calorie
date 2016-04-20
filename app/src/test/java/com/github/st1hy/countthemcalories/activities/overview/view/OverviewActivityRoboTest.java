package com.github.st1hy.countthemcalories.activities.overview.view;

import android.content.Intent;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class OverviewActivityRoboTest {
    private OverviewActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(OverviewActivity.class);
    }

    @Test
    public void canRunActivity() {
        assertThat(activity, notNullValue());
    }

    @Test
    public void canNavigateToIngredientsUsingMenu() throws InterruptedException {
        activity.navigationView.getMenu().performIdentifierAction(R.id.nav_ingredients, 0);
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, IngredientsActivity.class)));
    }

    @Test
    public void canOpenAddMeal() {
        activity.fab.performClick();
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, AddMealActivity.class)));
    }

}
