package com.github.st1hy.countthemcalories.database.unit;


import com.github.st1hy.countthemcalories.database.R;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.MASS;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_G;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KJ_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KJ_AT_G;
import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class GravimetricEnergyDensityUnitTest {

    @Test
    public void testPlurals() throws Exception {
        assertEquals(R.plurals.unit_kcal_at_100g, KCAL_AT_100G.getPluralResId());
        assertEquals(R.plurals.unit_kj_at_100g, KJ_AT_100G.getPluralResId());
        assertEquals(R.plurals.unit_kcal_at_1g, KCAL_AT_G.getPluralResId());
        assertEquals(R.plurals.unit_kj_at_1g, KJ_AT_G.getPluralResId());
    }

    @Test
    public void testUnitType() throws Exception {
        assertEquals(MASS, KCAL_AT_100G.getAmountUnitType());
        assertEquals(MASS, KJ_AT_100G.getAmountUnitType());
        assertEquals(MASS, KCAL_AT_G.getAmountUnitType());
        assertEquals(MASS, KJ_AT_G.getAmountUnitType());
    }

    @Test
    public void testConversionValues() throws Exception {
        assertEquals("0.04184", KCAL_AT_100G.conversionRate.toPlainString());
        assertEquals("0.01", KJ_AT_100G.conversionRate.toPlainString());
        assertEquals("4.184", KCAL_AT_G.conversionRate.toPlainString());
        assertEquals("1", KJ_AT_G.conversionRate.toPlainString());
    }

    @Test
    public void testConversion() throws Exception {
        BigDecimal value = KCAL_AT_100G.convertValue(BigDecimal.valueOf(300, 0), KJ_AT_G);
        Assert.assertEquals("12.552", value.toPlainString());
    }
}