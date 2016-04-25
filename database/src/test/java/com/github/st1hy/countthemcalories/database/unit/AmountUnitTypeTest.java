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
            assertThat(type, equalTo(AmountUnitType.fromId(id)));
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

    @Test(expected = IllegalArgumentException.class)
    public void testFromIdFailsOnWrongId() throws Exception {
        AmountUnitType.fromId(-1);
    }
}