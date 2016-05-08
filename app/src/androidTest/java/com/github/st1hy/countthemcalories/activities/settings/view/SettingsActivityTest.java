package com.github.st1hy.countthemcalories.activities.settings.view;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsActivityTest {

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<SettingsActivity> main = new IntentsTestRule<>(SettingsActivity.class);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    @Before
    public void setUp() throws Exception {
        SettingsActivity activity = main.getActivity();
        CaloriesCounterApplication application = (CaloriesCounterApplication) activity.getApplication();
        application.getComponent().getSettingsModel().resetToDefaultSettings();
    }

    @Test
    public void testLiquidSettings() throws Exception {
        onView(withText("kcal / 100 ml"))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withText(R.string.settings_select_preferred_energy_unit_for_drinks))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.settings_select_unit_dialog_title))
                .check(matches(isDisplayed()));
        onView(withText("1 kJ / ml"))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText("kJ / ml"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSolidSettings() throws Exception {
        onView(withText("kcal / 100 g"))
                .check(matches(isDisplayed()));
        onView(withText(R.string.settings_select_preferred_energy_unit_for_meals))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.settings_select_unit_dialog_title))
                .check(matches(isDisplayed()));
        onView(withText("1 kJ / g"))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText("kJ / g"))
                .check(matches(isDisplayed()));
    }
}