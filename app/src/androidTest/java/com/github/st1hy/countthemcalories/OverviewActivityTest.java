package com.github.st1hy.countthemcalories;

import android.content.ComponentName;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class OverviewActivityTest {
    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<OverviewActivity> main = new IntentsTestRule<>(OverviewActivity.class);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    @Test
    public void testActivityStart() {
        assertThat(main.getActivity(), notNullValue());
    }

    @Test
    public void testCanAddNewMeal() {
        onView(withId(R.id.overview_fab)).check(matches(isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), AddMealActivity.class)));
        onView(withId(R.id.add_meal_content)).check(matches(isDisplayed()));
        closeSoftKeyboard();
        pressBack();
        onView(withId(R.id.overview_toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void testDisplayTotalCalories() {
        //TODO
//        assertTrue(false);
//        onView(withId(R.id.overview_toolbar)).inRoot(with)
    }

    @Test
    public void testOpenSettingsWithMenu() {
        Espresso.openActionBarOverflowOrOptionsMenu(getTargetContext());
        onView(withText(R.string.action_settings))
                .check(matches(isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), SettingsActivity.class)));
    }
}