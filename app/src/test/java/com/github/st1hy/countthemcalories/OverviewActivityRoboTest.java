package com.github.st1hy.countthemcalories;

import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class OverviewActivityRoboTest {
    private OverviewActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(OverviewActivity.class);
    }

    @Test
    public void canRunActivity() {
        Assert.assertNotNull(activity);
    }

}
