package com.github.st1hy.countthemcalories.database.unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class EnergyDensityUtilsTest {

    @Test
    public void testGetUnits() throws Exception {
        assertArrayEquals(EnergyDensityUtils.getUnits(AmountUnitType.MASS), GravimetricEnergyDensityUnit.values());
        assertArrayEquals(EnergyDensityUtils.getUnits(AmountUnitType.VOLUME), VolumetricEnergyDensityUnit.values());
    }

    @Test
    public void testKcalConstant() throws Exception {
        assertEquals("4.184", EnergyDensityUtils.KJ_AT_GRAM_IN_KCAL_AT_GRAM.toPlainString());
        assertEquals("0.04184", EnergyDensityUtils.KJ_AT_G_IN_KCAL_AT_100_GRAM.toPlainString());
        assertEquals("1", EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_G.toPlainString());
        assertEquals("0.01", EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_100_G.toPlainString());
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