package com.github.st1hy.countthemcalories;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.idling.RxPicassoIdlingResource;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import rx.functions.Func0;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.st1hy.countthemcalories.matchers.ImageViewMatchers.withDrawable;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMealActivityTest {

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<AddMealActivity> main = new IntentsTestRule<>(AddMealActivity.class);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);



    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void testDisplaysTitle() {
        onView(allOf(withChild(withText(R.string.add_meal_title)), withId(R.id.add_meal_toolbar)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testCanSaveResult() {
        onView(withId(R.id.add_meal_save_button)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), OverviewActivity.class)));
    }

    @Test
    public void testSetMealName() {
        onView(withHint(R.string.add_meal_name_hint)).check(matches(isDisplayed()))
                .perform(typeTextIntoFocusedView("My meal"))
                .check(matches(withText("My meal")));
    }

    @Test
    public void testSelectImageFromGallery() {
        @DrawableRes @IdRes final int testDrawableId = android.R.drawable.ic_input_add;
        Matcher<Intent> intentMatcher = allOf(
                hasAction(Intent.ACTION_CHOOSER),
                hasExtras(
                        allOf(
                                hasEntry(Intent.EXTRA_INTENT,
                                        allOf(
                                                hasAction(Intent.ACTION_GET_CONTENT),
                                                hasType("image/*")
                                        )
                                ),
                                hasEntry(Intent.EXTRA_INITIAL_INTENTS,
                                        hasItemInArray(
                                                allOf(
                                                        hasAction(Intent.ACTION_PICK),
                                                        hasType("image/*")
                                                )
                                        )
                                )
                        )
                )
        );
        intending(intentMatcher).respondWith(new ActivityResult(AddMealActivity.REQUEST_PICK_IMAGE, new Func0<Intent>() {
            @Override
            public Intent call() {
                Intent intent = new Intent();
                intent.setData(resourceToUri(getContext(), testDrawableId));
                return intent;
            }
        }.call()));
        RxPicassoIdlingResource rxPicassoIdlingResource = RxPicassoIdlingResource.registerAndGet(
                main.getActivity().intoPicassoObservable());
        onView(withId(R.id.add_meal_image)).check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.add_meal_image_select_from_camera))
                .check(matches(isDisplayed()));
        onView(withText(R.string.add_meal_image_select_title))
                .check(matches(isDisplayed()));
        onView(withText(R.string.add_meal_image_select_from_gallery))
                .check(matches(isDisplayed()))
                .perform(click());
        intended(intentMatcher);
        onView(withId(R.id.add_meal_image))
                .check(matches(isDisplayed()))
                .check(matches(withDrawable(any(BitmapDrawable.class))));
        rxPicassoIdlingResource.unregister();
    }

    private static Uri resourceToUri (Context context, @IdRes int resID) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(context.getPackageName())
                .path(String.valueOf(resID))
                .build();
    }

}
