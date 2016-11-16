package com.github.st1hy.countthemcalories.activities.tags.view;

import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientActivityTest;
import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivityTest;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.st1hy.countthemcalories.actions.CTCViewActions.loopMainThreadForAtLeast;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TagsActivityTest {
    public static final Tag[] exampleTags = new Tag[]{
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
        OverviewActivityTest.addExampleMealsIngredientsTags(component.getDaoSession());
    }

    public static void addExampleTags(@NonNull DaoSession session) {
        TagDao tagDao = session.getTagDao();
        tagDao.deleteAll();
        tagDao.insertInTx(exampleTags);
        assertEquals(3, tagDao.loadAll().size());
    }

    @Test
    public void testAddNewTag() throws Exception {
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

//        onView(withText(R.string.tags_undo)).perform(click());
//        onView(withText(tagName)).check(doesNotExist());
    }

    @Test
    public void testRemoveTagUndo() throws Exception {
        main.launchActivity(null);
        final String tagName = exampleTags[1].getName();
        onView(withChild(withText(tagName))).perform(swipeRight());
        onView(allOf(withId(R.id.tags_item_delete), isDisplayed())).perform(click());
        onView(withText(R.string.tags_remove_dialog_title)).check(matches(isDisplayed()));
        onView(withText(R.string.tags_remove_information)).check(matches(isDisplayed()));
        onView(withText(android.R.string.no)).check(matches(isDisplayed()));
        onView(withText(android.R.string.yes)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(tagName)).check(doesNotExist());

        onView(withText(R.string.undo)).perform(click());
        onView(withText(tagName)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearch() throws InterruptedException {
        main.launchActivity(null);
        onView(withText(exampleTags[0].getName())).check(matches(isDisplayed()));
        onView(withText(exampleTags[1].getName())).check(matches(isDisplayed()));
        onView(withText(exampleTags[2].getName())).check(matches(isDisplayed()));
        onView(withId(R.id.tags_search_view)).perform(click());
        onView(withHint(R.string.search_hint))
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
        intent.putExtra(TagsActivity.EXTRA_EXCLUDE_TAG_STRING_ARRAY, new String[]{exampleTags[0].getName()});
        main.launchActivity(intent);
        onView(withText(exampleTags[0].getName())).check(doesNotExist());
        onView(withText(exampleTags[1].getName())).check(matches(isDisplayed()));
        onView(withText(exampleTags[2].getName())).check(matches(isDisplayed()));
    }

    @Test
    public void testEditTagName() throws Exception {
        main.launchActivity(null);
        final String tagName = exampleTags[1].getName();
        onView(withChild(withText(tagName))).perform(swipeLeft());
        onView(allOf(withId(R.id.tags_item_edit), isDisplayed())).perform(click());
        onView(withText(R.string.tags_edit_tag_dialog)).check(matches(isDisplayed()));
        onView(withHint(R.string.tags_new_tag_hint)).check(matches(isDisplayed()))
                .perform(click())
                .perform(clearText())
                .perform(typeTextIntoFocusedView("Edited tag"))
                .perform(closeSoftKeyboard());
        onView(withText(android.R.string.cancel)).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.tags_edit_tag_dialog)).check(doesNotExist());
        onView(withText(tagName)).check(doesNotExist());
        onView(withText("Edited tag")).check(matches(isDisplayed()));
    }

    @Test
    public void testOpenTaggedIngredients() throws Exception {
        main.launchActivity(null);
        final String tagName = exampleTags[1].getName();
        onView(withChild(withText(tagName))).perform(click());
        intended(allOf(hasComponent(new ComponentName(getTargetContext(), IngredientsActivity.class)),
                hasExtra(IngredientsActivity.EXTRA_TAG_FILTER_STRING, tagName)));

        onView(withId(R.id.ingredients_search_view)).check(matches(isDisplayed()));
        onView(withText(IngredientActivityTest.exampleIngredients[0].getName())).check(doesNotExist());
        onView(withText(IngredientActivityTest.exampleIngredients[1].getName())).check(matches(isDisplayed()));
        onView(withText(IngredientActivityTest.exampleIngredients[2].getName())).check(doesNotExist());

    }

    @Test
    public void testNewTagNameMatchesSearchQuery() {
        main.launchActivity(null);
        final String newTag = "Special tag";
        onView(withId(R.id.tags_search_view)).perform(click());
        onView(withHint(R.string.search_hint))
                .check(matches(isDisplayed()))
                .perform(click())
                .perform(typeTextIntoFocusedView(newTag))
                .perform(loopMainThreadForAtLeast(500));//debounce
        onView(withId(R.id.tags_empty)).check(matches(isDisplayed()));
        onView(withId(R.id.tags_add_new)).perform(click());
        onView(allOf(withId(R.id.tags_dialog_name), withText(newTag)))
                .check(matches(isDisplayed()));
    }
}
