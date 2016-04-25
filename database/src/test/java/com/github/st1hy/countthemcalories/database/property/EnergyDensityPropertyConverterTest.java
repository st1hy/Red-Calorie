package com.github.st1hy.countthemcalories.database.property;

import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

import static com.github.st1hy.countthemcalories.database.property.EnergyDensityPropertyConverter.getStandardizedUnit;
import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.MASS;
import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.VOLUME;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KJ_AT_G;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_100ML;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KJ_AT_ML;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class EnergyDensityPropertyConverterTest {
    final EnergyDensityPropertyConverter converter = new EnergyDensityPropertyConverter();

    @Test
    public void testConversion() throws Exception {
        EnergyDensity energyDensity = new EnergyDensity(KCAL_AT_100ML, BigDecimal.valueOf(404, 1));
        String databaseValue = converter.convertToDatabaseValue(energyDensity);
        EnergyDensity entityProperty = converter.convertToEntityProperty(databaseValue);
        assertNotNull(entityProperty);
        assertThat(entityProperty.getUnit(), CoreMatchers.<EnergyDensityUnit>equalTo(KJ_AT_ML));
        assertThat(energyDensity, equalTo(energyDensity.convertTo(KCAL_AT_100ML)));
    }

    @Test
    public void testStandardizedUnit() throws Exception {
        assertEquals(KJ_AT_G, getStandardizedUnit(MASS));
        assertEquals(KJ_AT_ML, getStandardizedUnit(VOLUME));
    }

    @Test
    public void testNullable() throws Exception {
        assertNull(converter.convertToDatabaseValue(null));
        assertNull(converter.convertToEntityProperty(null));

    }
}