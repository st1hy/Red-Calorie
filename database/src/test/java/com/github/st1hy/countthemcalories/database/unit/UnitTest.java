package com.github.st1hy.countthemcalories.database.unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class UnitTest {

    @Test
    public void testVolumeIdIntegrity() throws Exception {
        VolumeUnit[] values = VolumeUnit.values();
        for (VolumeUnit unit : values) {
            assertThat(VolumeUnit.fromId(unit.getId()), equalTo(unit));
        }
    }

    @Test
    public void testMassIfIntegrity() throws Exception {
        MassUnit[] values = MassUnit.values();
        for (MassUnit unit : values) {
            assertThat(MassUnit.fromId(unit.getId()), equalTo(unit));
        }
    }
}
