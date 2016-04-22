package com.github.st1hy.countthemcalories.core.permissions;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Locale;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import timber.log.BuildConfig;
import timber.log.Timber;

public class PermissionActor {
    private final Subject<Permission[], Permission[]> subject = new SerializedSubject<>(PublishSubject.<Permission[]>create());
    private final String[] requestedPermissionNames;

    /**
     * @param requestedPermissionNames array containing names of requested permissions
     * @throws IllegalArgumentException if array contain less than 1 element
     */
    public PermissionActor(@NonNull final String[] requestedPermissionNames) {
        if (requestedPermissionNames.length < 1)
            throw new IllegalArgumentException("Requested permissions cannot be empty");
        this.requestedPermissionNames = requestedPermissionNames;
    }

    public Observable<Permission[]> asObservable() {
        return subject.asObservable();
    }

    /**
     * @param permissionsNames permissions as from {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * @param grantResults     results as from {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * @throws IllegalArgumentException if permissionNames does not match this actor permissions,
     *                                  unless user cancelled request as specified in
     *                                  {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     */
    public void onRequestPermissionsResult(@NonNull String[] permissionsNames, @NonNull int[] grantResults) {
        checkReturningPermissionsNames(permissionsNames);
        Permission[] permissions = new Permission[requestedPermissionNames.length];
        if (grantResults.length == requestedPermissionNames.length) {
            for (int i = 0; i < requestedPermissionNames.length; i++) {
                permissions[i] = Permission.fromPermissionResult(grantResults[i]);
            }
        } else {
            if (BuildConfig.DEBUG)
                Timber.i("User canceled permission request, permissions: %s, results: %s",
                        Arrays.toString(permissions), Arrays.toString(grantResults));
            for (int i = 0; i < requestedPermissionNames.length; i++) {
                permissions[i] = Permission.REQUEST_CANCELED;
            }
        }
        subject.onNext(permissions);
        subject.onCompleted();
    }

    private void checkReturningPermissionsNames(@NonNull String[] permissionsNames) {
        if (permissionsNames.length > 0) {
            if (permissionsNames.length != requestedPermissionNames.length)
                throwIllegalArgument(permissionsNames);
            for (int i = 0; i < requestedPermissionNames.length; i++) {
                if (!requestedPermissionNames[i].equals(permissionsNames[i])) {
                    throwIllegalArgument(permissionsNames);
                }
            }
        }
    }

    private void throwIllegalArgument(@NonNull String[] permissionsNames) {
        throw new IllegalArgumentException(String.format(Locale.UK,
                "Returning permissions does not match this actor's permissions!\nExpected: %s\nGot: %s",
                Arrays.toString(requestedPermissionNames), Arrays.toString(permissionsNames)));
    }

}
