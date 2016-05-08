package com.github.st1hy.countthemcalories.activities.settings.view;


import com.github.st1hy.countthemcalories.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SettingsActivityRoboTest {

    private SettingsActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(SettingsActivity.class);
    }

    @Test
    public void testSetup() throws Exception {
        assertThat(activity, notNullValue());
        assertThat(activity.presenter, notNullValue());
        assertThat(activity.getComponent(), notNullValue());
        assertThat(activity.component, notNullValue());
        assertThat(activity.liquidUnitText, notNullValue());
        assertThat(activity.liquidUnitView, notNullValue());
        assertThat(activity.solidUnitText, notNullValue());
        assertThat(activity.solidUnitView, notNullValue());
    }

    @Test
    public void testOpenDialogWithLiquidUnitOptions() throws Exception {
        activity.liquidUnitView.performClick();
        ShadowDialog shadowDialog = shadowOf(RuntimeEnvironment.application).getLatestDialog();
        assertThat(shadowDialog, notNullValue());
    }

    @Test
    public void testOpenDialogWithSolidUnitOptions() throws Exception {
        activity.solidUnitView.performClick();
        ShadowDialog shadowDialog = shadowOf(RuntimeEnvironment.application).getLatestDialog();
        assertThat(shadowDialog, notNullValue());
    }
}