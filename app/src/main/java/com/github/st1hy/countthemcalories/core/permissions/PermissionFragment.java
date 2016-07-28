package com.github.st1hy.countthemcalories.core.permissions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.google.common.collect.Maps;

import java.util.Map;

import rx.Observable;

public class PermissionFragment extends Fragment implements PermissionSubject {
    public static final String TAG = PermissionFragment.class.getName();

    final Map<Integer, PermissionActor> pendingRequests = Maps.newHashMap();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public int checkSelfPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(getContext(), permission);
    }

    @NonNull
    @Override
    public Observable<Permission[]> requestPermission(@NonNull String[] permissions) {
        PermissionActor permissionActor = new PermissionActor(permissions);
        int requestId = pendingRequests.size();
        pendingRequests.put(requestId, permissionActor);
        requestPermissions(permissions, requestId);
        return permissionActor.asObservable();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Integer request = requestCode;
        PermissionActor permissionActor = pendingRequests.get(request);
        if (permissionActor != null) {
            pendingRequests.remove(request);
            permissionActor.onRequestPermissionsResult(permissions, grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}