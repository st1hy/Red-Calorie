package com.github.st1hy.countthemcalories.database.unit;

import com.github.st1hy.countthemcalories.database.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class FoodAmountUnitTest {

    @Test
    public void testPlurals() throws Exception {
        assertEquals(R.plurals.unit_gram, FoodAmountUnit.G.getPluralResId());
        assertEquals(R.plurals.unit_milliliter, FoodAmountUnit.ML.getPluralResId());
    }

    @Test
    public void testUnitType() throws Exception {
        assertEquals(AmountUnitType.MASS, FoodAmountUnit.G.getAmountUnitType());
        assertEquals(AmountUnitType.VOLUME, FoodAmountUnit.ML.getAmountUnitType());
    }
}