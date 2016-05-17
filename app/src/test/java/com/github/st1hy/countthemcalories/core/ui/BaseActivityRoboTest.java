package com.github.st1hy.countthemcalories.core.ui;


import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.notNullValue;

@Ignore("request permission is not supported by Robolectric")
@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class BaseActivityRoboTest {
    private BaseActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(BaseActivityImp.class);
    }

    @Test
    public void testActivityStart() throws Exception {
        assertThat(activity, notNullValue());
    }

    @Test
    public void testRequestPermission() {
        final String[] permission = {"test"};
        activity.requestPermission(permission).observeOn(Schedulers.immediate())
                .subscribe(new Action1<Permission[]>() {
                    @Override
                    public void call(Permission[] permissions) {
                        assertThat(permissions, arrayContaining(Permission.GRANTED));
                    }
                });
    }

    private static class BaseActivityImp extends BaseActivity {
    }

}
