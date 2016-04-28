package com.github.st1hy.countthemcalories.database.unit;

import com.github.st1hy.countthemcalories.database.R;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.VOLUME;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_100ML;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_ML;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KJ_AT_100ML;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KJ_AT_ML;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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

    @Test
    public void testIdUnique() throws Exception {
        VolumetricEnergyDensityUnit[] values = VolumetricEnergyDensityUnit.values();
        Set<Integer> ids = new HashSet<>(values.length);
        for (VolumetricEnergyDensityUnit unit : values) {
            ids.add(unit.getId());
        }
        assertThat(ids.size(), equalTo(values.length));
    }

    @Test
    public void testMatchingUnitsWithIds() {
        for (VolumetricEnergyDensityUnit type: VolumetricEnergyDensityUnit.values()) {
            int id = type.getId();
            assertThat(type, equalTo(VolumetricEnergyDensityUnit.fromId(id)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromIdFailsOnWrongId() throws Exception {
        VolumetricEnergyDensityUnit.fromId(-1);
    }
}