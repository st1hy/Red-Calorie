package com.github.st1hy.countthemcalories.activities.addingredient.view;


import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.core.PermissionHelper;
import com.github.st1hy.countthemcalories.core.rx.RxPicassoIdlingResource;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.st1hy.countthemcalories.actions.CTCViewActions.betterScrollTo;
import static com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivityTest.resourceToUri;
import static com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureActivityTestUtils.injectUriOnMatch;
import static com.github.st1hy.countthemcalories.matchers.CTCMatchers.galleryIntentMatcher;
import static com.github.st1hy.countthemcalories.matchers.ImageViewMatchers.withDrawable;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AddIngredientActivityTest {
    private static final Tag[] exampleTags = new Tag[]{
            new Tag(1L, "Tag 1"),
            new Tag(2L, "Tag 2"),
            new Tag(3L, "Meat"),
    };

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<AddIngredientActivity> main = new IntentsTestRule<>(AddIngredientActivity.class, true, false);


    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    @Before
    public void onSetUp() {
        ApplicationTestComponent component = (ApplicationTestComponent) ((CaloriesCounterApplication) getTargetContext().getApplicationContext()).getComponent();
        TagDao tagDao = component.getDaoSession().getTagDao();
        tagDao.deleteAll();
        tagDao.insertInTx(exampleTags);
        assertEquals(3, tagDao.loadAll().size());

        main.launchActivity(new Intent(AddIngredientType.DRINK.getAction()));

        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void testDisplaysElements() {
        onView(allOf(withChild(withText(R.string.add_ingredient_title)), withId(R.id.add_ingredient_toolbar)))
                .check(matches(isDisplayed()));
        onView(allOf(withText("kcal / 100 ml"), withId(R.id.add_ingredient_unit))).check(matches(isDisplayed()));
        onView(withHint(R.string.add_ingredient_name_hint)).check(matches(isDisplayed()))
                .check(matches(hasFocus()));
        onView(withHint(R.string.add_ingredient_energy_density_hint)).check(matches(isDisplayed()));
        onView(withId(R.id.add_ingredient_category_add)).check(matches(isDisplayed()));
    }

    @Test
    public void testSelectImageFromGallery() {
        Intent intent = new Intent();
        intent.setData(resourceToUri(getContext(), android.R.drawable.ic_input_add));
        intending(galleryIntentMatcher).respondWith(new ActivityResult(Activity.RESULT_OK, intent));
        RxPicassoIdlingResource rxPicassoIdlingResource = RxPicassoIdlingResource.registerAndGet();
        onView(withId(R.id.add_ingredient_image)).check(matches(isDisplayed()))
                .perform(click());
        PermissionHelper.allowPermissionsIfNeeded();
        onView(withText(R.string.add_ingredient_image_select_from_camera))
                .check(matches(isDisplayed()));
        onView(withText(R.string.add_ingredient_image_select_title))
                .check(matches(isDisplayed()));
        onView(withText(R.string.add_ingredient_image_select_from_gallery))
                .check(matches(isDisplayed()))
                .perform(click());
        intended(galleryIntentMatcher);
        onView(withId(R.id.add_ingredient_image))
                .check(matches(isDisplayed()))
                .check(matches(withDrawable(any(BitmapDrawable.class))));
        rxPicassoIdlingResource.unregister();
    }

    @Test
    public void testRemoveImage() throws Exception {
        testSelectImageFromGallery();

        onView(withId(R.id.add_ingredient_image_overlay_top)).check(matches(isDisplayed()));
        onView(withId(R.id.add_ingredient_image_overlay_bottom)).check(matches(isDisplayed()));
        onView(withId(R.id.add_ingredient_image)).perform(click());
        onView(withText(R.string.add_ingredient_image_remove)).perform(click());
        onView(withId(R.id.add_ingredient_image))
                .check(matches(isDisplayed()))
                .check(matches(withDrawable(not(any(BitmapDrawable.class)))));
        onView(withId(R.id.add_ingredient_image_overlay_top)).check(matches(not(isDisplayed())));
        onView(withId(R.id.add_ingredient_image_overlay_bottom)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testSelectImageFromGalleryUserCanceled() {
        intending(galleryIntentMatcher).respondWith(new ActivityResult(Activity.RESULT_CANCELED, null));
        onView(withId(R.id.add_ingredient_image)).check(matches(isDisplayed()))
                .perform(click());
        PermissionHelper.allowPermissionsIfNeeded();
        onView(withText(R.string.add_ingredient_image_select_from_gallery))
                .check(matches(isDisplayed()))
                .perform(click());
        intended(galleryIntentMatcher);
        //If doesn't crash is good
    }

    @Test
    public void testSelectImageFromCamera() throws Exception {
        AddIngredientActivity activity = main.getActivity();
        final Uri uri = resourceToUri(activity, android.R.drawable.ic_input_add);
        final Matcher<Intent> cameraIntentMatcher = allOf(
                hasAction(MediaStore.ACTION_IMAGE_CAPTURE),
                injectUriOnMatch(activity, uri)
        );

        intending(cameraIntentMatcher).respondWith(new ActivityResult(Activity.RESULT_OK, null));
        RxPicassoIdlingResource rxPicassoIdlingResource = RxPicassoIdlingResource.registerAndGet();
        onView(withId(R.id.add_ingredient_image)).check(matches(isDisplayed()))
                .perform(click());
        PermissionHelper.allowPermissionsIfNeeded();
        onView(withText(R.string.add_ingredient_image_select_from_camera))
                .check(matches(isDisplayed()))
                .perform(click());
        intended(cameraIntentMatcher);
        onView(withId(R.id.add_ingredient_image))
                .check(matches(isDisplayed()))
                .check(matches(withDrawable(any(BitmapDrawable.class))));
        rxPicassoIdlingResource.unregister();
    }

    @Test
    public void testSelectImageFromCameraUserCanceled() {
        final Matcher<Intent> cameraIntentMatcher = hasAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intending(cameraIntentMatcher).respondWith(new ActivityResult(Activity.RESULT_CANCELED, null));
        onView(withId(R.id.add_ingredient_image)).check(matches(isDisplayed()))
                .perform(click());
        PermissionHelper.allowPermissionsIfNeeded();
        onView(withText(R.string.add_ingredient_image_select_from_camera))
                .check(matches(isDisplayed()))
                .perform(click());
        intended(cameraIntentMatcher);
        //If doesn't crash is good
    }

    @Test
    public void testAddTag() throws Exception {
        closeSoftKeyboard();
        onView(withId(R.id.add_ingredient_category_add)).check(matches(isDisplayed()))
                .perform(betterScrollTo())
                .perform(click());
        onView(withId(R.id.tags_recycler)).check(matches(isDisplayed()));
        intended(hasAction(TagsActivity.ACTION_PICK_TAG));
        onView(withText(exampleTags[1].getName())).check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.add_ingredient_image)).check(matches(isDisplayed()));

        onView(withText(exampleTags[1].getName())).check(matches(isDisplayed()));
    }

    @Test
    public void testAddCurrentTagsToExcludedFromSearch() throws Exception {
        testAddTag();
        onView(withId(R.id.add_ingredient_category_add)).check(matches(isDisplayed()))
                .perform(click());
        intended(hasExtra(TagsActivity.EXTRA_EXCLUDE_TAG_STRING_ARRAY, new String[]{exampleTags[1].getName()}));
    }

    @Test
    public void testSearchIngredientOnTheInternet() throws Exception {
        onView(withHint(R.string.add_ingredient_name_hint))
                .perform(typeTextIntoFocusedView("Eggs"));
        closeSoftKeyboard();
        onView(withId(R.id.add_ingredient_name_search)).perform(click());
        Matcher<Intent> intentMatcher = allOf(hasAction(Intent.ACTION_VIEW),
                hasData("https://google.com/search?q=Eggs+calories"));
        intending(intentMatcher).respondWith(new ActivityResult(Activity.RESULT_CANCELED, null));
        intended(intentMatcher);
    }

    @Test
    public void testSelectIngredientType() throws Exception {
        closeSoftKeyboard();
        onView(allOf(withId(R.id.add_ingredient_unit), withText("kcal / 100 ml"))).perform(click());
        onView(withText("kcal / 100 ml")).check(matches(isDisplayed()));
        onView(withText("kcal / 100 g")).check(matches(isDisplayed()))
                .perform(click());
        onView(allOf(withId(R.id.add_ingredient_unit), withText("kcal / 100 g")))
                .check(matches(isDisplayed()));
    }
}
