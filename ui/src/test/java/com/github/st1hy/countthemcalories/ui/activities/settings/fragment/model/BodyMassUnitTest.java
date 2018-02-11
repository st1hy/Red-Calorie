package com.github.st1hy.countthemcalories.ui.activities.settings.fragment.model;

import com.github.st1hy.countthemcalories.ui.activities.settings.model.BodyMassUnit;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class BodyMassUnitTest {

    @Test
    public void testPound() throws Exception {
        assertThat(BodyMassUnit.POUND.getBase(), equalTo(0.45359237));
    }
}