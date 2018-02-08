package com.github.st1hy.countthemcalories.activities.addingredient.view;


import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.tags.TagsActivity;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.Tags;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.core.rx.RxImageLoadingIdlingResource;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;
import com.google.common.collect.Lists;

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
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.st1hy.countthemcalories.core.headerpicture.HeaderPicturePickerUtils.injectUriOnMatch;
import static com.github.st1hy.countthemcalories.core.headerpicture.TestPicturePicker.resourceToUri;
import static com.github.st1hy.countthemcalories.matchers.CTCMatchers.galleryIntentMatcher;
import static com.github.st1hy.countthemcalories.matchers.ImageViewMatchers.withDrawable;
import static com.github.st1hy.countthemcalories.matchers.ParcelMatchers.unparceled;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AddIngredientActivityTest {
    private static final Tag[] exampleTags = new Tag[]{
            new Tag(1L, "Tag 1"),
            new Tag(2L, "Tag 2"),
            new Tag(3L, "Meat"),
    };

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(
            getTargetContext());
    @SuppressWarnings("WeakerAccess")
    public final IntentsTestRule<AddIngredientActivity> main = new IntentsTestRule<>(
            AddIngredientActivity.class, true, false);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    @Before
    public void onSetUp() {
        CaloriesCounterApplication applicationContext = (CaloriesCounterApplication) getTargetContext()
                .getApplicationContext();
        ApplicationTestComponent component = (ApplicationTestComponent) applicationContext.getComponent();
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
        onView(allOf(withChild(withText(R.string.add_ingredient_title)), withId(R.id.image_header_toolbar)))
                .check(matches(isDisplayed()));
        onView(allOf(withText("kcal / 100 ml"), withId(R.id.add_ingredient_unit)))
                .check(matches(isDisplayed()));
        onView(withHint(R.string.add_ingredient_name_hint)).check(matches(isDisplayed()))
                .check(matches(hasFocus()));
        onView(withHint(R.string.add_ingredient_energy_density_hint)).check(matches(isDisplayed()));
        onView(withId(R.id.add_ingredient_fab_add_tag)).check(matches(isDisplayed()));
    }

    @Test
    public void testSelectImageFromGallery() {
        Intent intent = new Intent();
        intent.setData(resourceToUri(getContext(), android.R.drawable.ic_input_add));
        intending(galleryIntentMatcher).respondWith(new ActivityResult(Activity.RESULT_OK, intent));
        RxImageLoadingIdlingResource rxImageLoadingIdlingResource = RxImageLoadingIdlingResource.registerAndGet();
        onView(withId(R.id.image_header_image_view)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.add_ingredient_image_select_from_camera))
                .check(matches(isDisplayed()));
        onView(withText(R.string.add_ingredient_image_select_title))
                .check(matches(isDisplayed()));
        onView(withText(R.string.add_ingredient_image_select_from_gallery))
                .check(matches(isDisplayed()))
                .perform(click());
        intended(galleryIntentMatcher);
        onView(withId(R.id.image_header_image_view))
                .check(matches(isDisplayed()))
                .check(matches(withDrawable(any(BitmapDrawable.class))));
        rxImageLoadingIdlingResource.unregister();
    }

    @Test
    public void testRemoveImage() throws Exception {
        testSelectImageFromGallery();

        onView(withId(R.id.image_header_image_view)).perform(click());
        onView(withText(R.string.add_ingredient_image_remove)).perform(click());
        onView(withId(R.id.image_header_image_view))
                .check(matches(isDisplayed()))
                .check(matches(withDrawable(not(any(BitmapDrawable.class)))));
    }

    @Test
    public void testSelectImageFromGalleryUserCanceled() {
        intending(galleryIntentMatcher).respondWith(new ActivityResult(Activity.RESULT_CANCELED, null));
        onView(withId(R.id.image_header_image_view)).check(matches(isDisplayed()))
                .perform(click());
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
        RxImageLoadingIdlingResource rxImageLoadingIdlingResource = RxImageLoadingIdlingResource.registerAndGet();
        onView(withId(R.id.image_header_image_view)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.add_ingredient_image_select_from_camera))
                .check(matches(isDisplayed()))
                .perform(click());
        intended(cameraIntentMatcher);
        onView(withId(R.id.image_header_image_view))
                .check(matches(isDisplayed()))
                .check(matches(withDrawable(any(BitmapDrawable.class))));
        rxImageLoadingIdlingResource.unregister();
    }

    @Test
    public void testSelectImageFromCameraUserCanceled() {
        final Matcher<Intent> cameraIntentMatcher = hasAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intending(cameraIntentMatcher).respondWith(new ActivityResult(Activity.RESULT_CANCELED, null));
        onView(withId(R.id.image_header_image_view)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.add_ingredient_image_select_from_camera))
                .check(matches(isDisplayed()))
                .perform(click());
        intended(cameraIntentMatcher);
        //If doesn't crash is good
    }

    @Test
    public void testAddTag() throws Exception {
        closeSoftKeyboard();
        onView(withId(R.id.add_ingredient_fab_add_tag)).check(matches(isDisplayed()))
                .perform(click());
        onView(allOf(withId(R.id.tags_recycler), isAssignableFrom(RecyclerView.class))).check(matches(isDisplayed()));
        intended(hasAction(TagsActivity.ACTION_PICK_TAG));
        onView(withText(exampleTags[1].getName())).check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.tags_fab_confirm)).perform(click());
        onView(withId(R.id.image_header_image_view)).check(matches(isDisplayed()));

        onView(withText(exampleTags[1].getName())).check(matches(isDisplayed()));
    }

    @Test
    public void testAddCurrentTagsToSelectedFromSearch() throws Exception {
        testAddTag();
        onView(withId(R.id.add_ingredient_fab_add_tag)).check(matches(isDisplayed()))
                .perform(click());
        intended(hasExtra(equalTo(TagsActivity.EXTRA_SELECTED_TAGS),
                unparceled(equalTo(new Tags(Lists.newArrayList(exampleTags[1]))))));
    }

    @Test
    public void testSearchIngredientOnTheInternet() throws Exception {
        onView(withHint(R.string.add_ingredient_name_hint)).perform(typeTextIntoFocusedView("Eggs"));
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
