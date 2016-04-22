package com.github.st1hy.countthemcalories.core.permissions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static org.hamcrest.Matchers.arrayContaining;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class PermissionActorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionForEmptyPermissions() throws Exception {
        new PermissionActor(new String[]{});
    }

    @Test
    public void testReturnsPermissionsGranted() throws Exception {
        final String[] permissions = {"firstPermission", "secondPermission"};

        PermissionActor permissionActor = new PermissionActor(permissions);
        permissionActor.asObservable()
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<Permission[]>() {
                    @Override
                    public void call(Permission[] permissions) {
                        assertThat(permissions, arrayContaining(Permission.GRANTED, Permission.GRANTED));
                    }
                });

        permissionActor.onRequestPermissionsResult(permissions,
                new int[] {PERMISSION_GRANTED, PERMISSION_GRANTED});

    }

    @Test
    public void testReturnsRequestCanceled() throws Exception {
        final String[] permissions = {"firstPermission", "secondPermission"};

        PermissionActor permissionActor = new PermissionActor(permissions);
        permissionActor.asObservable()
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<Permission[]>() {
                    @Override
                    public void call(Permission[] permissions) {
                        assertThat(permissions, arrayContaining(Permission.REQUEST_CANCELED,
                                Permission.REQUEST_CANCELED));
                    }
                });

        permissionActor.onRequestPermissionsResult(new String[] {},
                new int[] {});

    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionWhenMismatchingPermissionsValues() throws Exception {

        final String[] permissions = {"firstPermission", "secondPermission"};

        PermissionActor permissionActor = new PermissionActor(permissions);
        permissionActor.onRequestPermissionsResult(new String[] {"second", "secondPermission"},
                new int[] {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionWhenMismatchingPermissionsAmount() throws Exception {

        final String[] permissions = {"firstPermission", "secondPermission"};

        PermissionActor permissionActor = new PermissionActor(permissions);
        permissionActor.onRequestPermissionsResult(new String[] {"second"},
                new int[] {});
    }
}