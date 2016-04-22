package com.github.st1hy.countthemcalories.core.permissions;

import android.content.pm.PackageManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.github.st1hy.countthemcalories.core.permissions.Permission.DENIED;
import static com.github.st1hy.countthemcalories.core.permissions.Permission.GRANTED;
import static com.github.st1hy.countthemcalories.core.permissions.Permission.fromPermissionResult;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class PermissionTest {

    @Test
    public void testFromPermissionResult() throws Exception {
        assertEquals(GRANTED, fromPermissionResult(PackageManager.PERMISSION_GRANTED));
        assertEquals(DENIED, fromPermissionResult(PackageManager.PERMISSION_DENIED));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromPermissionResultFailure() throws Exception {
        fromPermissionResult(Integer.MAX_VALUE);
    }
}