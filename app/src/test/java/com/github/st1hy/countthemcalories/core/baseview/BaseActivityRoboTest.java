package com.github.st1hy.countthemcalories.core.baseview;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;
import com.github.st1hy.countthemcalories.core.permissions.PermissionSubject;
import com.github.st1hy.countthemcalories.core.permissions.TestPermissionHelper;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class BaseActivityRoboTest {
    private BaseActivityImp activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(BaseActivityImp.class);
    }

    @Test
    public void testRequestPermission() {
        final String[] permission = {"test"};
        Observable<Permission[]> requestPermission = activity.permissionSubject
                .requestPermission(permission);
        TestPermissionHelper.grantPermission(activity, permission);
        Permission[] permissions = requestPermission.first().toBlocking().single();
        assertThat(permissions, arrayContaining(Permission.GRANTED));
    }

    private static class BaseActivityImp extends BaseActivity {
        PermissionSubject permissionSubject;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            permissionSubject = new PermissionModule().providePermissionSubject(getSupportFragmentManager());
        }
    }

}
