package com.github.st1hy.countthemcalories.activities.settings.view;


import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class SettingsActivityRoboTest {

    private SettingsActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(SettingsActivity.class);
    }

    @Test
    public void testChangeEnergySetting() throws Exception {
        assertThat(activity.energyHolder.unit.getText().toString(), equalTo("kcal"));
        activity.energyHolder.root.performClick();
        ShadowAlertDialog shadowDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        assertThat(shadowDialog, notNullValue());
        CharSequence[] sequences = new CharSequence[] {"kcal", "kJ"};
        assertThat(shadowDialog.getItems(), arrayContainingInAnyOrder(sequences));
        shadowDialog.clickOnItem(EnergyUnit.KJ.ordinal());
        assertThat(activity.energyHolder.unit.getText().toString(), equalTo("kJ"));
    }

    @Test
    public void testChangeMassSetting() throws Exception {
        assertThat(activity.massHolder.unit.getText().toString(), equalTo("100 g"));
        activity.massHolder.root.performClick();
        ShadowAlertDialog shadowDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        assertThat(shadowDialog, notNullValue());
        CharSequence[] sequences = new CharSequence[] {"100 g", "g"};
        assertThat(shadowDialog.getItems(), arrayContainingInAnyOrder(sequences));
        shadowDialog.clickOnItem(MassUnit.G.ordinal());
        assertThat(activity.massHolder.unit.getText().toString(), equalTo("g"));
    }

    @Test
    public void testChangeVolumeSetting() throws Exception {
        assertThat(activity.volumeHolder.unit.getText().toString(), equalTo("100 ml"));
        activity.volumeHolder.root.performClick();
        ShadowAlertDialog shadowDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        assertThat(shadowDialog, notNullValue());
        CharSequence[] sequences = new CharSequence[] {"ml", "100 ml"};
        assertThat(shadowDialog.getItems(), arrayContainingInAnyOrder(sequences));
        shadowDialog.clickOnItem(VolumeUnit.ML.ordinal());
        assertThat(activity.volumeHolder.unit.getText().toString(), equalTo("ml"));
    }
}