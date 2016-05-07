package com.github.st1hy.countthemcalories.activities.overview.view;

import android.content.Intent;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
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
        assertThat(activity.fab, notNullValue());
        assertThat(activity.presenter, notNullValue());
        assertThat(activity.getComponent(), notNullValue());
        assertThat(activity.component, notNullValue());
    }

    @Test
    public void canOpenAddMeal() {
        activity.fab.performClick();
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, AddMealActivity.class)));
    }

    @Test
    public void testMenuItems() throws Exception {
        shadowOf(activity).onCreateOptionsMenu(new RoboMenu());
        assertTrue(shadowOf(activity).clickMenuItem(R.id.action_settings));
        assertFalse(shadowOf(activity).clickMenuItem(-1));
    }


}
