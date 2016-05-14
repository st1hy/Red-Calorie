package com.github.st1hy.countthemcalories.database.property;

import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(JUnit4.class)
public class AmountUnitTypePropertyConverterTest {
    AmountUnitTypePropertyConverter converter = new AmountUnitTypePropertyConverter();

    @Test
    public void testConversion() throws Exception {
        for (AmountUnitType type : AmountUnitType.values()) {
            Integer integer = converter.convertToDatabaseValue(type);
            assertThat(converter.convertToEntityProperty(integer), equalTo(type));
        }
    }

    @Test
    public void testNullValue() throws Exception {
        assertThat(converter.convertToEntityProperty(null), equalTo(AmountUnitType.UNKNOWN));

    }
}