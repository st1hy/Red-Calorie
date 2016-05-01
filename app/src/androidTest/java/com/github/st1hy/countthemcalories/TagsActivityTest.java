package com.github.st1hy.countthemcalories;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TagsActivityTest {

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<TagsActivity> main = new IntentsTestRule<>(TagsActivity.class);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    @Test
    public void testAddRemoveNewTag() throws Exception {
        final String tagName = "My tag name";
        onView(withId(R.id.tags_add_new)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.tags_new_tag_dialog)).check(matches(isDisplayed()));
        onView(withHint(R.string.tags_new_tag_hint)).check(matches(isDisplayed()))
                .perform(ViewActions.typeTextIntoFocusedView(tagName));
        onView(withText(tagName)).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.tags_recycler)).check(matches(isDisplayed()))
                .perform(actionOnItem(withChild(withText(tagName)), longClick()));
        onView(withText(R.string.tags_remove_dialog_title)).check(matches(isDisplayed()));
        onView(withText(R.string.tags_remove_information)).check(matches(isDisplayed()));
        onView(withText(android.R.string.no)).check(matches(isDisplayed()));
        onView(withText(android.R.string.yes)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(tagName)).check(doesNotExist());
    }
}
