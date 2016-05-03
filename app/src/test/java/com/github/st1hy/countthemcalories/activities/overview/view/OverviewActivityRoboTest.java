package com.github.st1hy.countthemcalories.activities.overview.view;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.testrunner.RxRobolectricGradleTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;

import rx.plugins.TestRxPlugins;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RxRobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class OverviewActivityRoboTest {
    private OverviewActivity activity;

    @Before
    public void setup() {
        TestRxPlugins.registerImmediateHook();
        activity = Robolectric.setupActivity(OverviewActivity.class);
    }

    @Test
    public void canRunActivity() {
        assertThat(activity, notNullValue());
        assertThat(activity.drawer, notNullValue());
        assertThat(activity.fab, notNullValue());
        assertThat(activity.navigationView, notNullValue());
        assertThat(activity.presenter, notNullValue());
        assertThat(activity.toolbar, notNullValue());
        assertThat(activity.getComponent(), notNullValue());
        assertThat(activity.component, notNullValue());
        assertThat(activity.drawerToggle, notNullValue());
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
    public void testMenuItems() throws Exception {
        shadowOf(activity).onCreateOptionsMenu(new RoboMenu());
        assertTrue(shadowOf(activity).clickMenuItem(R.id.action_settings));
        assertFalse(shadowOf(activity).clickMenuItem(-1));
    }

    @Test
    public void testOpenSettings() throws Exception {
        shadowOf(activity).onCreateOptionsMenu(new RoboMenu());
        assertTrue(shadowOf(activity).clickMenuItem(R.id.action_settings));
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, SettingsActivity.class)));
    }

    @Test
    public void testOpenCategories() throws Exception {
        activity.navigationView.getMenu().performIdentifierAction(R.id.nav_tags, 0);
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, TagsActivity.class)));
    }

    @Test
    public void testOpenSettingsWithMenu() throws Exception {
        activity.navigationView.getMenu().performIdentifierAction(R.id.nav_settings, 0);
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, SettingsActivity.class)));

    }
}
