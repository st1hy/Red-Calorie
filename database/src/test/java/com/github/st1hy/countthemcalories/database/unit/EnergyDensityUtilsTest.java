package com.github.st1hy.countthemcalories.database.unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class EnergyDensityUtilsTest {

    @Test
    public void testGetUnits() throws Exception {
        assertArrayEquals(EnergyDensityUtils.getUnits(AmountUnitType.MASS), GravimetricEnergyDensityUnit.values());
        assertArrayEquals(EnergyDensityUtils.getUnits(AmountUnitType.VOLUME), VolumetricEnergyDensityUnit.values());
    }

    @Test
    public void testGetString() throws Exception {
        for (AmountUnitType unitType: AmountUnitType.values()) {
            EnergyDensityUnit[] units = (EnergyDensityUnit[]) EnergyDensityUtils.getUnits(unitType);
            for (EnergyDensityUnit unit: units) {
                String string = EnergyDensityUtils.getString(unit);
                assertThat(unit, equalTo(EnergyDensityUtils.fromString(string)));
            }
        }
    }
}