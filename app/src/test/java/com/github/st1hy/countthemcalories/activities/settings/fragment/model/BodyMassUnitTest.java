package com.github.st1hy.countthemcalories.activities.settings.fragment.model;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class BodyMassUnitTest {

    @Test
    public void testPound() throws Exception {
        assertThat(BodyMassUnit.POUND.getBase().toPlainString(), equalTo("0.45359237"));
    }
}