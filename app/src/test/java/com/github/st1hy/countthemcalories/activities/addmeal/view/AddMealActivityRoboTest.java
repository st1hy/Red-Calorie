package com.github.st1hy.countthemcalories.activities.addmeal.view;

import com.github.st1hy.countthemcalories.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AddMealActivityRoboTest {
    private AddMealActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(AddMealActivity.class);
    }


    @Test
    public void testActivityStart() {
        assertThat(activity, notNullValue());
        assertThat(activity.presenter, notNullValue());
        assertThat(activity.picasso, notNullValue());
        assertThat(activity.toolbar, notNullValue());
        assertThat(activity.getComponent(), notNullValue());
        assertThat(activity.component, notNullValue());
        assertThat(activity.picassoLoaderCallback, notNullValue());
        assertThat(activity.subscriptions, notNullValue());
    }

}
