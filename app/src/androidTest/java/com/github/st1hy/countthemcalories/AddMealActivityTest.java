package com.github.st1hy.countthemcalories;

import android.content.ComponentName;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class AddMealActivityTest {

    @Rule
    public final IntentsTestRule<AddMealActivity> main = new IntentsTestRule<>(AddMealActivity.class);

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
}
