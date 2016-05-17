package com.github.st1hy.countthemcalories.activities.settings.model;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import rx.Subscription;
import rx.functions.Action1;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class SettingsModelTest {
    private SettingsModel model;

    @Before
    public void setUp() throws Exception {
        CaloriesCounterApplication application = (CaloriesCounterApplication) RuntimeEnvironment.application;
        model = application.getComponent().getSettingsModel();
        model.getPreferences().edit().clear().apply();
    }

    @Test
    public void testGetEnergyUnit() throws Exception {
        assertThat(model.getEnergyUnit(), equalTo(EnergyUnit.KCAL));
        model.setEnergyUnit(EnergyUnit.KJ);
        assertThat(model.getEnergyUnit(), equalTo(EnergyUnit.KJ));
    }

    @Test
    public void testGetMassUnit() throws Exception {
        assertThat(model.getMassUnit(), equalTo(MassUnit.G100));
        model.setMassUnit(MassUnit.G);
        assertThat(model.getMassUnit(), equalTo(MassUnit.G));
    }

    @Test
    public void testGetVolumeUnit() throws Exception {
        assertThat(model.getVolumeUnit(), equalTo(VolumeUnit.ML100));
        model.setVolumeUnit(VolumeUnit.ML);
        assertThat(model.getVolumeUnit(), equalTo(VolumeUnit.ML));
    }


    @Test
    public void testResetToDefaultSettings() throws Exception {
        @SuppressWarnings("unchecked")
        Action1<Object> mock = (Action1<Object>) Mockito.mock(Action1.class);

        model.setEnergyUnit(EnergyUnit.KJ);
        model.setMassUnit(MassUnit.G);
        model.setVolumeUnit(VolumeUnit.ML);
        Subscription subscription = model.toObservable().subscribe(mock);
        model.resetToDefaultSettings();
        subscription.unsubscribe();
        verify(mock).call(isA(UnitChangedEvent.Energy.class));
        verify(mock).call(isA(UnitChangedEvent.Mass.class));
        verify(mock).call(isA(UnitChangedEvent.Volume.class));
        assertThat(SettingsModel.defaultUnitOfEnergy, equalTo(model.getEnergyUnit()));
        assertThat(SettingsModel.defaultUnitOfMass, equalTo(model.getMassUnit()));
        assertThat(SettingsModel.defaultUnitOfVolume, equalTo(model.getVolumeUnit()));
    }

    @Test
    public void testGetUnitName() throws Exception {
        assertEquals("kcal", model.getUnitName(EnergyUnit.KCAL));
        assertEquals("kJ", model.getUnitName(EnergyUnit.KJ));
        assertEquals("g", model.getUnitName(MassUnit.G));
        assertEquals("100 g", model.getUnitName(MassUnit.G100));
        assertEquals("ml", model.getUnitName(VolumeUnit.ML));
        assertEquals("100 ml", model.getUnitName(VolumeUnit.ML100));
    }

    @Test
    public void testGetUnitBy() throws Exception {
        assertThat(model.getAmountUnitFrom(AmountUnitType.VOLUME), Matchers.<AmountUnit>equalTo(SettingsModel.defaultUnitOfVolume));
        assertThat(model.getAmountUnitFrom(AmountUnitType.MASS), Matchers.<AmountUnit>equalTo(SettingsModel.defaultUnitOfMass));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetAmountByException() throws Exception {
        model.getAmountUnitFrom(AmountUnitType.UNKNOWN);    }

}