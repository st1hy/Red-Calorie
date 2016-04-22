package com.github.st1hy.countthemcalories.core.permissions;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.core.Utils;

import java.util.Locale;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class PermissionsHelper {
    private final PermissionSubject subject;

    @Inject
    public PermissionsHelper(@NonNull PermissionSubject subject) {
        this.subject = subject;
    }

    @NonNull
    public Observable<Permission> checkPermissionAndAskIfNecessary(@NonNull String permission,
            @Nullable RequestRationale requestRationale) {
        if (Utils.hasMarshmellow()) {
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
                .first()
                .flatMap(new Func1<UserResponseForRationale, Observable<Permission>>() {
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
                });
    }


    @NonNull
    private Observable<Permission> makeRequestFor(@NonNull final String permission) {
        return subject.requestPermission(new String[] {permission})
                .first()
                .map(new Func1<Permission[], Permission>() {
                    @Override
                    public Permission call(Permission[] permissions) {
                        if (permissions.length != 1) {
                            throw new IllegalArgumentException(String.format(Locale.UK,
                                    "Asked for one permission, expected 1 answer; got %d", permissions.length));
                        }
                        return permissions[0];
                    }
                });
    }

}
