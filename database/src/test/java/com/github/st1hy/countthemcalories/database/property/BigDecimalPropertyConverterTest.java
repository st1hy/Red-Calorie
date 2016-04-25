package com.github.st1hy.countthemcalories.database.property;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class BigDecimalPropertyConverterTest {
    final BigDecimalPropertyConverter converter = new BigDecimalPropertyConverter();

    @Test
    public void testConversion() throws Exception {
        final BigDecimal value = BigDecimal.valueOf(232325,-1);
        String databaseValue = converter.convertToDatabaseValue(value);
        BigDecimal fromDatabase = converter.convertToEntityProperty(databaseValue);
        assertNotNull(fromDatabase);
        assertEquals(value.toPlainString(), fromDatabase.toPlainString());
    }

    @Test
    public void testNullValues() throws Exception {
        assertNull(converter.convertToDatabaseValue(null));
        assertNull(converter.convertToEntityProperty(null));
    }

}