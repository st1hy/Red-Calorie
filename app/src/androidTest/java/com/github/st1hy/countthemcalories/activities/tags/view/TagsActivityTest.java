package com.github.st1hy.countthemcalories.activities.tags.view;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.widget.SearchView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.hamcrest.Matchers;
import org.junit.After;
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
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TagsActivityTest {
    private static final String[] exampleTags = {"Tag 1", "Tag 2", "Meat"};

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<TagsActivity> main = new IntentsTestRule<>(TagsActivity.class);
    private final DbProcessingIdleResource idlingDbProcess = new DbProcessingIdleResource();

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    @Before
    public void setUp() throws Exception {
        ApplicationTestComponent component = (ApplicationTestComponent) ((CaloriesCounterApplication) getTargetContext().getApplicationContext()).getComponent();
        TagDao tagDao = component.getDaoSession().getTagDao();
        tagDao.deleteAll();
        tagDao.insertInTx(new Tag(null, exampleTags[0]), new Tag(null, exampleTags[1]), new Tag(null, exampleTags[2]));
        assertEquals(3, tagDao.loadAll().size());
        component.getTagsModel().getDbProcessingObservable().subscribe(idlingDbProcess);
        Espresso.registerIdlingResources(idlingDbProcess.getIdlingResource());
    }

    @After
    public void tearDown() throws Exception {
        idlingDbProcess.unsubscribe();
        Espresso.unregisterIdlingResources(idlingDbProcess.getIdlingResource());
    }

    @Test
    public void testAddRemoveNewTag() throws Exception {
        final String tagName = "My tag name";
        onView(ViewMatchers.withId(R.id.tags_add_new)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.tags_new_tag_dialog)).check(matches(isDisplayed()));
        onView(withHint(R.string.tags_new_tag_hint)).check(matches(isDisplayed()))
                .perform(ViewActions.typeTextIntoFocusedView(tagName));
        onView(withText(tagName)).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(tagName)).check(matches(isDisplayed()))
                .perform(longClick());
        onView(withText(R.string.tags_remove_dialog_title)).check(matches(isDisplayed()));
        onView(withText(R.string.tags_remove_information)).check(matches(isDisplayed()));
        onView(withText(android.R.string.no)).check(matches(isDisplayed()));
        onView(withText(android.R.string.yes)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(tagName)).check(doesNotExist());
    }


    @Test
    public void testSearch() throws InterruptedException {
        idlingDbProcess.waitForStart();
        onView(withText(exampleTags[0])).check(matches(isDisplayed()));
        onView(withText(exampleTags[1])).check(matches(isDisplayed()));
        onView(withText(exampleTags[2])).check(matches(isDisplayed()));
        onView(withClassName(Matchers.equalTo(SearchView.class.getName())))
                .check(matches(isDisplayed()))
                .perform(click())
                .perform(typeTextIntoFocusedView("Tag"));
        synchronized (this) {
            wait(500); //debounce
        }
        onView(withText(exampleTags[0])).check(matches(isDisplayed()));
        onView(withText(exampleTags[1])).check(matches(isDisplayed()));
        onView(withText(exampleTags[2])).check(doesNotExist());
    }
}
