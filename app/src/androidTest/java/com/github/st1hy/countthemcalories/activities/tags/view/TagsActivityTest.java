package com.github.st1hy.countthemcalories.activities.tags.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.widget.SearchView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.st1hy.countthemcalories.actions.CTCViewActions.loopMainThreadForAtLeast;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TagsActivityTest {
    public static final Tag[] exampleTags = new Tag[] {
            new Tag(1L, "Tag 1"),
            new Tag(2L, "Tag 2"),
            new Tag(3L, "Meal")
    };

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<TagsActivity> main = new IntentsTestRule<>(TagsActivity.class, true, false);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    @Before
    public void setUp() throws Exception {
        ApplicationTestComponent component = (ApplicationTestComponent) ((CaloriesCounterApplication) getTargetContext().getApplicationContext()).getComponent();
        addExampleTags(component.getDaoSession());
    }

    public static void addExampleTags(@NonNull DaoSession session) {
        TagDao tagDao = session.getTagDao();
        tagDao.deleteAll();
        tagDao.insertInTx(exampleTags);
        assertEquals(3, tagDao.loadAll().size());
    }

    @Test
    public void testAddNewTagUndo() throws Exception {
        main.launchActivity(null);
        final String tagName = "My tag name";
        onView(ViewMatchers.withId(R.id.tags_add_new)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.tags_new_tag_dialog)).check(matches(isDisplayed()));
        onView(withHint(R.string.tags_new_tag_hint)).check(matches(isDisplayed()))
                .perform(ViewActions.typeTextIntoFocusedView(tagName));
        onView(withText(tagName)).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(tagName)).check(matches(isDisplayed()));

        onView(withText(R.string.tags_undo)).perform(click());
        onView(withText(tagName)).check(doesNotExist());
    }

    @Test
    public void testRemoveTagUndo() throws Exception {
        main.launchActivity(null);
        final String tagName = exampleTags[1].getName();
        onView(withText(tagName)).check(matches(isDisplayed()))
                .perform(longClick());
        onView(withText(R.string.tags_remove_dialog_title)).check(matches(isDisplayed()));
        onView(withText(R.string.tags_remove_information)).check(matches(isDisplayed()));
        onView(withText(android.R.string.no)).check(matches(isDisplayed()));
        onView(withText(android.R.string.yes)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(tagName)).check(doesNotExist());

        onView(withText(R.string.tags_undo)).perform(click());
        onView(withText(tagName)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearch() throws InterruptedException {
        main.launchActivity(null);
        onView(withText(exampleTags[0].getName())).check(matches(isDisplayed()));
        onView(withText(exampleTags[1].getName())).check(matches(isDisplayed()));
        onView(withText(exampleTags[2].getName())).check(matches(isDisplayed()));
        onView(withClassName(Matchers.equalTo(SearchView.class.getName())))
                .check(matches(isDisplayed()))
                .perform(click())
                .perform(typeTextIntoFocusedView("Tag"))
                .perform(loopMainThreadForAtLeast(500));//debounce
        onView(withText(exampleTags[0].getName())).check(matches(isDisplayed()));
        onView(withText(exampleTags[1].getName())).check(matches(isDisplayed()));
        onView(withText(exampleTags[2].getName())).check(doesNotExist());
    }

    @Test
    public void testExcludeTags() throws Exception {
        Intent intent = new Intent(TagsActivity.ACTION_PICK_TAG);
        intent.putExtra(TagsActivity.EXTRA_EXCLUDE_TAG_IDS, new long[] {exampleTags[0].getId()});
        main.launchActivity(intent);
        onView(withText(exampleTags[0].getName())).check(doesNotExist());
        onView(withText(exampleTags[1].getName())).check(matches(isDisplayed()));
        onView(withText(exampleTags[2].getName())).check(matches(isDisplayed()));
    }

}
