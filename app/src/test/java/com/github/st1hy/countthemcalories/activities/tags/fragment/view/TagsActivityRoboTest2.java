package com.github.st1hy.countthemcalories.activities.tags.fragment.view;

import android.content.ComponentName;
import android.content.Intent;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.tags.fragment.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.github.st1hy.countthemcalories.testutils.TimberUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class TagsActivityRoboTest2 {

    private TagsActivity activity;
    private TagsFragment fragment;

    @Before
    public void setup() throws Exception {
        Timber.plant(TimberUtils.ABOVE_WARN);
        TagsDaoAdapter.debounceTime = 0;
        TestRxPlugins.registerImmediateHookIO();
        TagsActivityRoboTest.addExampleTags(OverviewActivityRoboTest.prepareDatabase());
    }

    private Intent getPickTagIntentWithExclude() {
        Intent intent = new Intent(TagsActivity.ACTION_PICK_TAG);
        intent.putExtra(TagsActivity.EXTRA_EXCLUDE_TAG_STRING_ARRAY, new String[]{TagsActivityRoboTest.exampleTags[0].getName()});
        return intent;
    }

    private void setupActivity(Intent intent) {
        activity = Robolectric.buildActivity(TagsActivity.class)
                .withIntent(intent)
                .setup()
                .get();
        fragment = (TagsFragment) activity.getSupportFragmentManager()
                .findFragmentByTag("tags content");
    }

    @After
    public void tearDown() throws Exception {
        Timber.uprootAll();
        TestRxPlugins.reset();
        TagsDaoAdapter.debounceTime = 250;
    }

    @Test
    public void testExcludeTags() throws Exception {
        setupActivity(getPickTagIntentWithExclude());
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(3));
    }

    @Test
    public void testOpenIngredientsFilteredByTag() throws Exception {
        setupActivity(null);
        fragment.recyclerView.getChildAt(0).findViewById(R.id.tag_item_button).performClick();

        assertThat(shadowOf(activity).getNextStartedActivity(), allOf(
                hasComponent(new ComponentName(activity, IngredientsActivity.class)),
                hasExtraWithKey(IngredientsActivity.EXTRA_TAG_FILTER_STRING)));

    }
}