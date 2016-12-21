package com.github.st1hy.countthemcalories.core.permissions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.core.Utils;

import javax.inject.Inject;

import rx.Observable;

public class PermissionsHelper {
    private final PermissionSubject subject;
    private final Utils utils;
    private final PersistentPermissionCache permissionCache;

    @Inject
    public PermissionsHelper(@NonNull PermissionSubject subject,
                             @NonNull Utils utils,
                             @NonNull PersistentPermissionCache permissionCache) {
        this.subject = subject;
        this.utils = utils;
        this.permissionCache = permissionCache;
    }

    /**
     * Checks permission depending on API version. If permission is DENIED and have not been asked \
     * in this session; shows question to the user to enable it, else returns current permission.
     * <p>
     * For use when checking permission without direct user interaction.
     *
     * @param permissionName name of the permission
     * @return observable on permission.
     */
    @NonNull
    public Observable<Permission> checkPermissionAndAskOnce(@NonNull String permissionName) {
        if (utils.hasMarshmallow()) {
            Permission actualPermission = checkPermission(permissionName);
            if (actualPermission == Permission.GRANTED) {
                return Observable.just(Permission.GRANTED);
            } else {
                if (hasAskedForPermissionBefore(permissionName)) {
                    return Observable.just(actualPermission);
                } else
                    return makeRequestFor(permissionName);
            }
        } else {
            return Observable.just(Permission.GRANTED);
        }
    }

    /**
     * Checks permission depending on API version. Always asks permission if its not enabled.
     * <p>
     * For use when permission is required by direct user action.
     *
     * @param permissionName   name of the permission
     * @param requestRationale optional rationale about this permission, may be null
     * @return observable on permission
     */
    @NonNull
    public Observable<Permission> checkPermissionAndAskIfNecessary(@NonNull String permissionName,
                                                                   @Nullable RequestRationale requestRationale) {
        if (utils.hasMarshmallow()) {
            Permission actualPermission = checkPermission(permissionName);
            switch (actualPermission) {
                case GRANTED:
                    return Observable.just(Permission.GRANTED);
                case DENIED:
                    return maybeShowRequestRationale(permissionName, requestRationale);
                default:
                    throw new IllegalStateException("Check permission should return either GRANTED or DENIED");
            }
        } else {
            return Observable.just(Permission.GRANTED);
        }
    }

    private boolean hasAskedForPermissionBefore(@NonNull String permissionName) {
        return permissionCache.get(permissionName) != null;
    }

    @NonNull
    private Permission checkPermission(@NonNull String permission) {
        int checkResult = subject.checkSelfPermission(permission);
        return Permission.fromPermissionResult(checkResult);
    }

    @NonNull
    private Observable<Permission> maybeShowRequestRationale(@NonNull String permission,
                                                             @Nullable RequestRationale requestRationale) {
        if (requestRationale != null && subject.shouldShowRequestPermissionRationale(permission)) {
            return showRequestRationale(permission, requestRationale);
        } else {
            return makeRequestFor(permission);
        }
    }

    @NonNull
    private Observable<Permission> showRequestRationale(@NonNull final String permission,
                                                        @NonNull RequestRationale requestRationale) {
        return requestRationale.showRationale()
                .flatMap(userResponseForRationale -> {
                    switch (userResponseForRationale) {
                        case CONTINUE_WITH_REQUEST:
                            return makeRequestFor(permission);
                        case ABORT_REQUEST:
                        default:
                            return Observable.just(Permission.REQUEST_CANCELED);
                    }
                });
    }


    @NonNull
    private Observable<Permission> makeRequestFor(@NonNull final String permissionName) {
        return subject.requestPermission(new String[]{permissionName})
                .filter(permissions -> permissions.length == 1)
                .map(permissions -> permissions[0])
                .filter(permission -> permission != Permission.REQUEST_CANCELED)
                .doOnNext(permission -> permissionCache.put(permissionName, permission));
    }

}
