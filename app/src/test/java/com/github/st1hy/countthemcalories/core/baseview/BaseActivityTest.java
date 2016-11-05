package com.github.st1hy.countthemcalories.core.baseview;

import com.github.st1hy.countthemcalories.core.Utils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BaseActivityTest {

    @Test
    public void testAssertNotNull() {
        final Object expected = new Object();
        Object output = Utils.assertNotNull(expected);
        Assert.assertEquals(expected, output);
    }

    @Test(expected = NullPointerException.class)
    public void testAssertThrowsException() {
        Utils.assertNotNull(null);
    }
}