package com.github.st1hy.countthemcalories.database.unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Hashtable;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class AmountUnitTypeTest {

    @Test
    public void testMatchingUnitsWithIds() {
        for (AmountUnitType type: AmountUnitType.values()) {
            int id = type.getId();
            assertThat(AmountUnitType.fromId(id), equalTo(type));
        }
    }

    @Test
    public void testUnitIdsUnique() throws Exception {
        AmountUnitType[] values = AmountUnitType.values();
        Map<Integer, AmountUnitType> units = new Hashtable<>(values.length);

        for (AmountUnitType unit : values) {
            int id = unit.getId();
            assertThat(units.get(id), nullValue());
            units.put(id, unit);
        }
    }

    @Test
    public void testFromUnknownId() throws Exception {
        assertThat(AmountUnitType.fromId(-1323), equalTo(AmountUnitType.UNKNOWN));
        assertThat(AmountUnitType.fromId(0x423), equalTo(AmountUnitType.UNKNOWN));
    }
}