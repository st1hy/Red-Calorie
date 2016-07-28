package com.github.st1hy.countthemcalories.core.permissions;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestPermissionHelper {

    public static void grantPermission(@NonNull BaseActivity activity, @NonNull String[] permissions) {
        PermissionFragment fragment = (PermissionFragment) activity.getSupportFragmentManager()
                .findFragmentByTag(PermissionFragment.TAG);
        assertThat(fragment.pendingRequests.size(), equalTo(1));
        int[] grant = new int[permissions.length];
        Arrays.fill(grant, PackageManager.PERMISSION_GRANTED);
        int request = fragment.pendingRequests.keySet().iterator().next();
        fragment.onRequestPermissionsResult(request, permissions, grant);
    }

    public static void grantPermission(@NonNull BaseActivity activity, @NonNull String permission) {
        grantPermission(activity, new String[] {permission});
    }
}
