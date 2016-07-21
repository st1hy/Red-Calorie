package com.github.st1hy.countthemcalories.core.baseview;


import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.observables.ConnectableObservable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class BaseActivityRoboTest {
    private BaseActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(BaseActivityImp.class);
    }

    @Test
    public void testRequestPermission() {
        final String[] permission = {"test"};
        ConnectableObservable<Permission[]> requestPermission = activity.requestPermission(permission).replay();
        requestPermission.connect();
        TestPermissionHelper.grantPermission(activity, permission);
        Permission[] permissions = requestPermission.toBlocking().single();
        assertThat(permissions, arrayContaining(Permission.GRANTED));
    }

    private static class BaseActivityImp extends BaseActivity {
    }

}
