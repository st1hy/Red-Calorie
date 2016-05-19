package com.github.st1hy.countthemcalories.activities.tags.view;

import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
@LargeTest
public class TagsActivityRoboTest2 {

    private  TagsActivity activity;
    private final Timber.Tree tree = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(tag +" " + message);
        }
    };

    @Before
    public void setup() throws Exception {
        Timber.plant(tree);
        TagsDaoAdapter.debounceTime = 0;
        TestRxPlugins.registerImmediateHookIO();
        TagsActivityRoboTest.addExampleTags(OverviewActivityRoboTest.prepareDatabase());

        Intent intent = new Intent(TagsActivity.ACTION_PICK_TAG);
        intent.putExtra(TagsActivity.EXTRA_EXCLUDE_TAG_IDS, new long[] { TagsActivityRoboTest.exampleTags[0].getId()});
        activity = Robolectric.buildActivity(TagsActivity.class)
                .withIntent(intent)
                .setup()
                .get();
    }

    @After
    public void tearDown() throws Exception {
        Timber.uproot(tree);
        TestRxPlugins.reset();
        TagsDaoAdapter.debounceTime = 250;
    }


    @Test
    public void testExcludeTags() throws Exception {
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
    }
}