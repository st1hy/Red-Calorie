package com.github.st1hy.countthemcalories.activities.tags.view;


import android.content.Intent;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.testrunner.RxRobolectricGradleTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import rx.plugins.TestRxPlugins;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(RxRobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TagsActivityRoboTest2 {

    private TagsActivity activity;

    @Before
    public void setup() {
        TestRxPlugins.reset();
        Intent intent = new Intent(TagsTestActivity.ACTION_PICK_TAG);
        activity = Robolectric.buildActivity(TagsTestActivity.class)
                .withIntent(intent)
                .setup()
                .get();
    }


    @Test
    public void testSearch() throws Exception {
        TestRxPlugins.reset();
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));

        activity.searchView.performClick();
        activity.searchView.setQuery("Tag", true);

        synchronized (this) {
            wait(500); //Wait for debounce on computation scheduler
        }
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
    }

}
