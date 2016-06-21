package com.github.st1hy.countthemcalories.core;

import android.os.Build;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.widget.Button;

import timber.log.Timber;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class PermissionHelper {

    public static void allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector()
                    .className(Button.class)
                    .textMatches("[aA][lL][lL][oO][wW]"));
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click();
                } catch (UiObjectNotFoundException e) {
                    Timber.e(e, "There is no permissions dialog to interact with ");
                }
            }
        }
    }
}
