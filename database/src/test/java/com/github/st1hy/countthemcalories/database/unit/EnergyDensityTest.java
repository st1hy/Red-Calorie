package com.github.st1hy.countthemcalories.database.unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_G;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KJ_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KJ_AT_G;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class EnergyDensityTest {
    final EnergyDensityUnit unit = KCAL_AT_100G;
    final BigDecimal value = BigDecimal.valueOf(300, 0);
    final EnergyDensity energyDensity = new EnergyDensity(unit, value);

    @Test
    public void testCreation() throws Exception {
        assertThat(unit, equalTo(energyDensity.getUnit()));
        assertThat(value, equalTo(energyDensity.getValue()));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testEquals() throws Exception {
        final BigDecimal value2 = BigDecimal.valueOf(301, 0);
        final EnergyDensity energyDensity2 = new EnergyDensity(unit, value2);
        assertTrue(energyDensity.equals(energyDensity));
        assertTrue(energyDensity.equals(new EnergyDensity(energyDensity)));
        assertFalse(energyDensity.equals(energyDensity2));
    }

    @Test
    public void testHashCode() throws Exception {
        final BigDecimal value2 = BigDecimal.valueOf(301, 0);
        final EnergyDensity energyDensity2 = new EnergyDensity(unit, value2);
        assertEquals(energyDensity.hashCode(), energyDensity.hashCode());
        assertEquals(energyDensity.hashCode(), new EnergyDensity(energyDensity).hashCode());
        assertNotEquals(energyDensity.hashCode(), energyDensity2.hashCode());
    }

    @Test
    public void testConversion() throws Exception {
        BigDecimal value = energyDensity.convertTo(KJ_AT_G).getValue();
        assertEquals("12.552", value.toPlainString());
        value = energyDensity.convertTo(KJ_AT_100G).getValue();
        assertEquals("1255.2", value.toPlainString());
        value = energyDensity.convertTo(KCAL_AT_G).getValue();
        assertEquals("3", value.toPlainString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConversionFails() throws Exception {
        energyDensity.convertTo(VolumetricEnergyDensityUnit.KCAL_AT_100ML);
    }

    @Test
    public void testGetPlural() throws Exception {
        assertEquals(KCAL_AT_100G.getFormatResId(), energyDensity.getFormatResId());
    }

}