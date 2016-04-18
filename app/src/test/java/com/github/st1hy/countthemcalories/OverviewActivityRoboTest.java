package com.github.st1hy.countthemcalories;

import android.support.v4.view.GravityCompat;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        Assert.assertNotNull(activity);
    }

    @Test
    public void testNavigateToOverview() {
        openDrawerMenu();

    }


    private void openDrawerMenu() {
        assertThat(activity.drawer, is(notNullValue()));
        activity.drawer.openDrawer(GravityCompat.START);
        assertTrue(activity.drawer.isDrawerOpen(GravityCompat.START));

    }
}
