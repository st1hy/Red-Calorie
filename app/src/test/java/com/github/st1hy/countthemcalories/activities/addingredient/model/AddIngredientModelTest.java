package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.os.Bundle;

import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import rx.functions.Action1;

import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.STATE_UNIT;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KJ_AT_100ML;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddIngredientModelTest {

    final GravimetricEnergyDensityUnit expectedDefault = KCAL_AT_100G;
    final VolumetricEnergyDensityUnit expectedDefaultLiquid = KJ_AT_100ML;

    @Mock
    private SettingsModel settingsModel;
    @Mock
    private Bundle bundle;
    private AddIngredientModel model;

    @Before
    public void setUp() throws Exception {
        when(settingsModel.getPreferredGravimetricUnit()).thenReturn(expectedDefault);
        when(settingsModel.getPreferredVolumetricUnit()).thenReturn(expectedDefaultLiquid);
        model = new AddIngredientModel(settingsModel, null);
    }

    @Test
    public void testGetUnit() throws Exception {
        when(bundle.getString(STATE_UNIT)).thenReturn(EnergyDensityUtils.getString(expectedDefaultLiquid));
        EnergyDensityUnit unit = model.getUnit(bundle);
        assertThat(expectedDefaultLiquid, equalTo(unit));
    }

    @Test
    public void testOnSaveState() throws Exception {
        model.onSaveState(bundle);
        verify(bundle).putInt(STATE_UNIT, expectedDefault.getId());
    }

    @Test
    public void testGetUnitSelection() throws Exception {
        EnergyDensityUnit[] unitSelection = model.getUnitSelection();
        assertThat(unitSelection, Matchers.<EnergyDensityUnit>arrayContaining(expectedDefault, expectedDefaultLiquid));
    }

    @Test
    public void testGetUnitSelectionOptions() throws Exception {
        when(settingsModel.getUnitName(expectedDefault)).thenReturn("1");
        when(settingsModel.getUnitName(expectedDefaultLiquid)).thenReturn("2");
        String[] options = model.getUnitSelectionOptions();
        assertThat(options, arrayContaining("1", "2"));

    }

    @Test
    public void testSetUnit() throws Exception {
        final AtomicInteger callCount = new AtomicInteger(0);
        EnergyDensityUnit expectedValue = expectedDefault;
        final AtomicReference<EnergyDensityUnit> expected = new AtomicReference<>(expectedValue);
        model.getUnitObservable().subscribe(new Action1<EnergyDensityUnit>() {
            @Override
            public void call(EnergyDensityUnit energyDensityUnit) {
                callCount.incrementAndGet();
                assertThat(expected.get(), equalTo(energyDensityUnit));
            }
        });
        expectedValue = VolumetricEnergyDensityUnit.KCAL_AT_ML;
        expected.set(expectedValue);
        model.setUnit(expected.get());
        assertEquals(2, callCount.get());

    }
}