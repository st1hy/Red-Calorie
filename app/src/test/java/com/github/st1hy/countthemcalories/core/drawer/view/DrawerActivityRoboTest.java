package com.github.st1hy.countthemcalories.core.drawer.view;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Scheduler;
import rx.plugins.TestRxPlugins;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class DrawerActivityRoboTest {
    private DrawerActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(OverviewActivity.class);
        TestRxPlugins.registerImmediateMainThreadHook();
        Schedulers.registerHook(new Schedulers.HookImp() {
            @Override
            public Scheduler computation() {
                return immediate();
            }

            @Override
            public Scheduler io() {
                return immediate();
            }
        });
        assertThat(activity, notNullValue());
        assertThat(activity.toolbar, notNullValue());
        assertThat(activity.presenter, notNullValue());
    }

    @After
    public void tearDown() throws Exception {
        TestRxPlugins.reset();
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
    public void testOpenCategories() throws Exception {
        activity.navigationView.getMenu().performIdentifierAction(R.id.nav_tags, 0);
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, hasComponent(new ComponentName(activity, TagsActivity.class)));
    }

    @Test
    public void testOpenIngredients() throws Exception {
        activity.navigationView.getMenu().performIdentifierAction(R.id.nav_ingredients, 0);
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, hasComponent(new ComponentName(activity, IngredientsActivity.class)));
    }

    @Test
    public void testOpenSettings() throws Exception {
        activity.navigationView.getMenu().performIdentifierAction(R.id.nav_settings, 0);
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, hasComponent(new ComponentName(activity, SettingsActivity.class)));
    }

    @Test
    public void testOpenOverview() throws Exception {
        DrawerActivity activity = Robolectric.setupActivity(IngredientsActivity.class);
        activity.navigationView.getMenu().performIdentifierAction(R.id.nav_overview, 0);
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, hasComponent(new ComponentName(activity, OverviewActivity.class)));
    }

    @Test
    public void testOnStop() throws Exception {
        IngredientsPresenter presenterMock = Mockito.mock(IngredientsPresenter.class);
        activity.presenter = presenterMock;
        activity.onStop();
        verify(presenterMock).onStop();
    }

    @Test
    public void testOnStart() throws Exception {
        IngredientsPresenter presenterMock = Mockito.mock(IngredientsPresenter.class);
        activity.presenter = presenterMock;
        activity.onStart();
        verify(presenterMock).onStart();
    }
}
