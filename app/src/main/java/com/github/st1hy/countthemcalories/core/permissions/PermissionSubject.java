package com.github.st1hy.countthemcalories.core.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import rx.Observable;

public interface PermissionSubject {

    /**
     * Determine whether <em>you</em> have been granted a particular permission.
     *
     * @param permission The name of the permission being checked.
     *
     * @return {@link PackageManager#PERMISSION_GRANTED} if you have the
     * permission, or {@link PackageManager#PERMISSION_DENIED} if not.
     *
     * @see Context#checkSelfPermission(String)
     */
    int checkSelfPermission(@NonNull String permission);


    /**
     * Gets whether you should show UI with rationale for requesting a permission.
     * You should do this only if you do not have the permission and the context in
     * which the permission is requested does not clearly communicate to the user
     * what would be the benefit from granting this permission.
     * <p>
     * For example, if you write a camera app, requesting the camera permission
     * would be expected by the user and no rationale for why it is requested is
     * needed. If however, the app needs location for tagging photos then a non-tech
     * savvy user may wonder how location is related to taking photos. In this case
     * you may choose to show UI with rationale of requesting this permission.
     * </p>
     *
     * @param permission A permission your app wants to request.
     * @return Whether you can show permission rationale UI.
     *
     * @see #checkSelfPermission(String)
     * @see Activity#shouldShowRequestPermissionRationale(String)
     */
    boolean shouldShowRequestPermissionRationale(@NonNull String permission);


    /**
     * Performs permission request. Will require user response to continue.
     *
     * Realization of this functionality can be done by calling {@link Activity#requestPermissions(String[], int)}
     * and waiting for the response in callback {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     *
     * Implementation must wrap this async call into observable.
     *
     * @param permissions array of permissions app requests
     * @return observable permission result.
     * @see Activity#requestPermissions(String[],int)
     */
    @NonNull
    Observable<Permission[]> requestPermission(@NonNull String[] permissions);
}
