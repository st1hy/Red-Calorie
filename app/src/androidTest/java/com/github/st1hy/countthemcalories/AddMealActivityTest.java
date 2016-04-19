package com.github.st1hy.countthemcalories;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AddMealActivityTest {

    @Rule
    public final IntentsTestRule<AddMealActivity> main = new IntentsTestRule<>(AddMealActivity.class);

    @Test
    public void testDisplaysTitle() {
        onView(withId(R.id.add_meal_toolbar)).check(matches(withText(R.string.add_meal_title)));
    }

    @Test
    public void testCanSaveResult() {
//        onView(withId(R.id.add_meal_save_button)).perform(click());
    }
}
