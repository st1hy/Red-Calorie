package com.github.st1hy.countthemcalories.core.permissions;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import rx.functions.Func1;

public enum Permission {
    GRANTED, DENIED, REQUEST_CANCELED;

    private static final Func1<Permission, Boolean> IS_GRANTED = permission ->
            permission == Permission.GRANTED;

    private static final Func1<Permission, Boolean> IS_NOT_GRANTED = permission ->
            permission != Permission.GRANTED;

    /**
     * @return either GRANTED or DENIED
     */
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

    @NonNull
    public static Func1<Permission, Boolean> isGranted() {
        return IS_GRANTED;
    }

    @NonNull
    public static Func1<Permission, Boolean> notGranted() {
        return IS_NOT_GRANTED;
    }
}
