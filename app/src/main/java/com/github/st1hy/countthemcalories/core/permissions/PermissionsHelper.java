package com.github.st1hy.countthemcalories.core.permissions;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.core.Utils;

import java.util.Locale;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

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
     * Checks permission depending on API version. If permission have not been asked in this session
     * shows question to the user to enable it, else returns cached result.
     * <p/>
     * For use when checking permission without direct user interaction.
     *
     * @param permission name of the permission
     * @return observable on permission.
     */
    @NonNull
    public Observable<Permission> checkPermissionAndAskOnce(@NonNull String permission) {
        if (utils.hasMarshmallow()) {
            Permission cachedPermission = permissionCache.get(permission);
            if (cachedPermission != null)
                return Observable.just(cachedPermission);
            else
                return checkPermissionAndAskIfNecessaryApi23(permission, null);
        } else {
            return Observable.just(Permission.GRANTED);
        }
    }

    /**
     * Checks permission depending on API version. Always asks permission if its not enabled.
     * <p/>
     * For use when permission is required by direct user action.
     *
     * @param permission name of the permission
     * @param requestRationale optional rationale about this permission, may be null
     * @return observable on permission
     */
    @NonNull
    public Observable<Permission> checkPermissionAndAskIfNecessary(@NonNull String permission,
                                                                   @Nullable RequestRationale requestRationale) {
        if (utils.hasMarshmallow()) {
            return checkPermissionAndAskIfNecessaryApi23(permission, requestRationale);
        } else {
            return Observable.just(Permission.GRANTED);
        }
    }

    @NonNull
    private Observable<Permission> checkPermissionAndAskIfNecessaryApi23(@NonNull String permission,
                                                                         @Nullable RequestRationale requestRationale) {
        int checkResult = subject.checkSelfPermission(permission);
        switch (checkResult) {
            case PackageManager.PERMISSION_GRANTED:
                return Observable.just(Permission.GRANTED);
            case PackageManager.PERMISSION_DENIED:
                return maybeShowRequestRationale(permission, requestRationale);
            default:
                throw new IllegalArgumentException("Unknown answer for checked permission");
        }
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
                .flatMap(onRationaleResult(permission));
    }

    @NonNull
    private Func1<UserResponseForRationale, Observable<Permission>> onRationaleResult(@NonNull final String permission) {
        return new Func1<UserResponseForRationale, Observable<Permission>>() {
            @Override
            public Observable<Permission> call(UserResponseForRationale userResponseForRationale) {
                switch (userResponseForRationale) {
                    case CONTINUE_WITH_REQUEST:
                        return makeRequestFor(permission);
                    case ABORT_REQUEST:
                    default:
                        return Observable.just(Permission.REQUEST_CANCELED);
                }
            }
        };
    }


    @NonNull
    private Observable<Permission> makeRequestFor(@NonNull final String permissionName) {
        return subject.requestPermission(new String[]{permissionName})
                .map(onlyOne())
                .doOnNext(cachePermission(permissionName));
    }

    @NonNull
    private Func1<Permission[], Permission> onlyOne() {
        return new Func1<Permission[], Permission>() {
            @Override
            public Permission call(Permission[] permissions) {
                if (permissions.length != 1) {
                    throw new IllegalArgumentException(String.format(Locale.UK,
                            "Asked for one permission, expected 1 answer; got %d", permissions.length));
                }
                return permissions[0];
            }
        };
    }

    @NonNull
    private Action1<Permission> cachePermission(@NonNull final String permissionName) {
        return new Action1<Permission>() {
            @Override
            public void call(Permission permission) {
                permissionCache.put(permissionName, permission);
            }
        };
    }

}
