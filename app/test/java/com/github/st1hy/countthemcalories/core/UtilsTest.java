package com.github.st1hy.countthemcalories.core;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {
    private static final int MARSHMALLOW = Build.VERSION_CODES.M;

    private final Utils utils = new Utils();

    @Test
    public void testHasMarshmallow() throws Exception {
        for (int i = 1; i < MARSHMALLOW; i++) {
            setBuildVersion(i);
            assertFalse(utils.hasMarshmallow());
        }
        for (int i = MARSHMALLOW; i < 100; i++) {
            setBuildVersion(i);
            assertTrue(utils.hasMarshmallow());
        }
    }

    private void setBuildVersion(int version) {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", version);
    }

}