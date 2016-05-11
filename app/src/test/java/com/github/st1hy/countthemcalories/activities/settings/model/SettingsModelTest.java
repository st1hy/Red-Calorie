package com.github.st1hy.countthemcalories.activities.settings.model;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;

import rx.Subscription;
import rx.functions.Action1;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SettingsModelTest {
    private SettingsModel model;

    @Before
    public void setUp() throws Exception {
        CaloriesCounterApplication application = (CaloriesCounterApplication) RuntimeEnvironment.application;
        model = application.getComponent().getSettingsModel();
    }

    @Test
    public void testPreferredVolumetricUnit() throws Exception {
        VolumetricEnergyDensityUnit expected = VolumetricEnergyDensityUnit.KJ_AT_ML;
        model.setPreferredVolumetricUnit(expected);
        VolumetricEnergyDensityUnit unit = model.getPreferredVolumetricUnit();
        assertThat(expected, equalTo(unit));
    }

    @Test
    public void testPreferredGravimetricUnit() throws Exception {
        GravimetricEnergyDensityUnit expected = GravimetricEnergyDensityUnit.KJ_AT_G;
        model.setPreferredGravimetricUnit(expected);
        GravimetricEnergyDensityUnit unit = model.getPreferredGravimetricUnit();
        assertThat(expected, equalTo(unit));
    }

    @Test
    public void testSetDefaultSettingsIfNotSet() throws Exception {
        model.setDefaultSettingsIfNotSet();
        assertThat(SettingsModel.defaultGravimetricEnergyUnit, equalTo(model.getPreferredGravimetricUnit()));
        assertThat(SettingsModel.defaultVolumetricEnergyUnit, equalTo(model.getPreferredVolumetricUnit()));
    }

    @Test
    public void testResetToDefaultSettings() throws Exception {
        @SuppressWarnings("unchecked")
        Action1<Object> mock = (Action1<Object>) Mockito.mock(Action1.class);

        model.setPreferredGravimetricUnit(GravimetricEnergyDensityUnit.KJ_AT_G);
        model.setPreferredVolumetricUnit(VolumetricEnergyDensityUnit.KJ_AT_ML);
        Subscription subscription = model.toObservable().subscribe(mock);
        model.resetToDefaultSettings();
        subscription.unsubscribe();
        verify(mock).call(isA(EnergyUnit.Volume.class));
        verify(mock).call(isA(EnergyUnit.Mass.class));
        assertThat(SettingsModel.defaultGravimetricEnergyUnit, equalTo(model.getPreferredGravimetricUnit()));
        assertThat(SettingsModel.defaultVolumetricEnergyUnit, equalTo(model.getPreferredVolumetricUnit()));
    }

    @Test
    public void testGetDefaultsWithoutPresetting() throws Exception {
        assertThat(SettingsModel.defaultGravimetricEnergyUnit, equalTo(model.getPreferredGravimetricUnit()));
        assertThat(SettingsModel.defaultVolumetricEnergyUnit, equalTo(model.getPreferredVolumetricUnit()));
    }

    @Test
    public void testGeneratingUnitName() throws Exception {
        String unitName = model.getUnitName(GravimetricEnergyDensityUnit.KJ_AT_G, BigDecimal.ONE);
        assertEquals("1 kJ / g", unitName);
    }

    @Test
    public void testUnitPlural() throws Exception {
        String unitName = model.getUnitName(GravimetricEnergyDensityUnit.KJ_AT_G);
        assertEquals("kJ / g", unitName);
    }
}