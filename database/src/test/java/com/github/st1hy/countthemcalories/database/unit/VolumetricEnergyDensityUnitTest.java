package com.github.st1hy.countthemcalories.database.unit;

import com.github.st1hy.countthemcalories.database.R;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.VOLUME;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_100ML;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_ML;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KJ_AT_100ML;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KJ_AT_ML;
import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class VolumetricEnergyDensityUnitTest {

    @Test
    public void testPlurals() throws Exception {
        assertEquals(R.plurals.unit_kcal_at_100ml, KCAL_AT_100ML.getPluralResId());
        assertEquals(R.plurals.unit_kj_at_100ml, KJ_AT_100ML.getPluralResId());
        assertEquals(R.plurals.unit_kcal_at_1ml, KCAL_AT_ML.getPluralResId());
        assertEquals(R.plurals.unit_kj_at_1ml, KJ_AT_ML.getPluralResId());
    }

    @Test
    public void testUnitType() throws Exception {
        assertEquals(VOLUME, KCAL_AT_100ML.getAmountUnitType());
        assertEquals(VOLUME, KJ_AT_100ML.getAmountUnitType());
        assertEquals(VOLUME, KCAL_AT_ML.getAmountUnitType());
        assertEquals(VOLUME, KJ_AT_ML.getAmountUnitType());
    }

    @Test
    public void testConversion() throws Exception {
        BigDecimal value = KCAL_AT_100ML.convertValue(BigDecimal.valueOf(300, 0), KJ_AT_ML);
        Assert.assertEquals("12.552", value.toPlainString());
    }
}