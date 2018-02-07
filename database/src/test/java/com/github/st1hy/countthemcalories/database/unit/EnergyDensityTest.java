package com.github.st1hy.countthemcalories.database.unit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class EnergyDensityTest {
    static final double DELTA = 0.0001;
    final EnergyUnit energyUnit = EnergyUnit.KCAL;
    final AmountUnit unit = MassUnit.G100;
    final double value = 300.0;
    final EnergyDensity energyDensity = new EnergyDensity(energyUnit, unit, value);

    @Test
    public void testCreation() throws Exception {
        assertThat(energyDensity.getEnergyUnit(), equalTo(energyUnit));
        assertThat(energyDensity.getAmountUnit(), equalTo(unit));
        assertThat(energyDensity.getValue(), equalTo(value));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testEquals() throws Exception {
        final double value2 = 301.0;
        final EnergyDensity energyDensity2 = new EnergyDensity(energyUnit, unit, value2);
        assertTrue(energyDensity.equals(energyDensity));
        assertTrue(energyDensity.equals(new EnergyDensity(energyDensity)));
        assertFalse(energyDensity.equals(energyDensity2));
    }

    @Test
    public void testHashCode() throws Exception {
        final double value2 = 301.0;
        final EnergyDensity energyDensity2 = new EnergyDensity(energyUnit, unit, value2);
        assertEquals(energyDensity.hashCode(), energyDensity.hashCode());
        assertEquals(energyDensity.hashCode(), new EnergyDensity(energyDensity).hashCode());
        assertNotEquals(energyDensity.hashCode(), energyDensity2.hashCode());
    }

    @Test
    public void testConversion() throws Exception {
        double value = energyDensity.convertTo(EnergyUnit.KJ, MassUnit.G).getValue();
        assertEquals(12.552, value, DELTA);
        value = energyDensity.convertTo(EnergyUnit.KJ, MassUnit.G100).getValue();
        assertEquals(1255.2, value, DELTA);
        value = energyDensity.convertTo(EnergyUnit.KCAL, MassUnit.G).getValue();
        assertEquals(3, value, DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConversionFails() throws Exception {
        energyDensity.convertTo(EnergyUnit.KJ, VolumeUnit.ML);
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertThat(energyDensity.toString().isEmpty(), equalTo(false));

    }
}