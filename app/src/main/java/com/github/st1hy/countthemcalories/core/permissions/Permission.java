package com.github.st1hy.countthemcalories.core.permissions;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

public enum Permission {
    GRANTED, DENIED, REQUEST_CANCELED;

    @NonNull
    public static Permission fromPermissionResult(int packageManagerResponse) {
        switch (packageManagerResponse) {
            case PackageManager.PERMISSION_GRANTED:
                return GRANTED;
            case PackageManager.PERMISSION_DENIED:
                return DENIED;
            default:
                throw new IllegalArgumentException("Unknown package manager response");
        }
    }
}
