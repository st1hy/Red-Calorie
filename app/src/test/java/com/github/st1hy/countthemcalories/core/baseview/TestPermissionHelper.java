package com.github.st1hy.countthemcalories.core.baseview;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestPermissionHelper {

    public static void grantPermission(@NonNull BaseActivity activity, @NonNull String[] permissions) {
        assertThat(activity.pendingPermissionRequests.size(), equalTo(1));
        int[] grant = new int[permissions.length];
        Arrays.fill(grant, PackageManager.PERMISSION_GRANTED);
        int request = activity.pendingPermissionRequests.keyAt(0);
        activity.onRequestPermissionsResult(request, permissions, grant);
    }

    public static void grantPermission(@NonNull BaseActivity activity, @NonNull String permission) {
        grantPermission(activity, new String[] {permission});
    }
}
