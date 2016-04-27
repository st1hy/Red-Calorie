package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class IngredientsActivityRoboTest {

    private IngredientsActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(IngredientsActivity.class);
    }

    @Test
    public void testActivityStart() {
        assertThat(activity, notNullValue());
        assertThat(activity.presenter, notNullValue());
        assertThat(activity.navigationView, notNullValue());
        assertThat(activity.toolbar, notNullValue());
        assertThat(activity.getComponent(), notNullValue());
        assertThat(activity.component, notNullValue());
        assertThat(activity.drawer, notNullValue());
    }

    @Test
    public void canNavigateToIngredientsUsingMenu() throws InterruptedException {
        activity.navigationView.getMenu().performIdentifierAction(R.id.nav_overview, 0);
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, OverviewActivity.class)));
    }

    @Test
    public void canNavigateToSettingsUsingMenu() throws InterruptedException {
        activity.navigationView.getMenu().performIdentifierAction(R.id.nav_settings, 0);
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, SettingsActivity.class)));
    }

    @Test
    public void testBackPressed() throws Exception {
        activity.onBackPressed();
        assertTrue(shadowOf(activity).isFinishing());
    }

    @Test
    public void testBackClosesDrawer() throws Exception {
        DrawerLayout drawerLayoutMock = Mockito.mock(DrawerLayout.class);
        when(drawerLayoutMock.isDrawerOpen(GravityCompat.START)).thenReturn(true);
        activity.drawer = drawerLayoutMock;
        activity.onBackPressed();
        verify(drawerLayoutMock).closeDrawer(GravityCompat.START);
        assertFalse(shadowOf(activity).isFinishing());
    }

    @Test
    public void testAddIngredient() throws Exception {
        activity.fab.performClick();

        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, hasComponent(new ComponentName(activity, AddIngredientActivity.class)));
    }
}