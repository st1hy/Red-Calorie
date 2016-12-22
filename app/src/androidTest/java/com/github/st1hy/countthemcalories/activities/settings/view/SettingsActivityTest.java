package com.github.st1hy.countthemcalories.activities.settings.view;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.SettingsActivity;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.junit.After;
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


    @After
    public void tearDown() throws Exception {
        CaloriesCounterApplication application = (CaloriesCounterApplication) main.getActivity().getApplication();
        ApplicationTestComponent component = (ApplicationTestComponent) application.getComponent();
        component.getSettingsModel().resetToDefaultSettings();
    }

    @Test
    public void testEnergySettings() throws Exception {
        onView(withText("kcal")).check(matches(isDisplayed()));
        onView(ViewMatchers.withText(R.string.settings_unit_energy_title))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.settings_select_unit_dialog_title))
                .check(matches(isDisplayed()));
        onView(withText("kJ"))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText("kJ"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testMassSettings() throws Exception {
        onView(withText("100 g")).check(matches(isDisplayed()));
        onView(ViewMatchers.withText(R.string.settings_unit_mass_title))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.settings_select_unit_dialog_title))
                .check(matches(isDisplayed()));
        onView(withText("g"))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText("g"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testVolumeSettings() throws Exception {
        onView(withText("100 ml")).check(matches(isDisplayed()));
        onView(ViewMatchers.withText(R.string.settings_unit_volume_title))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.settings_select_unit_dialog_title))
                .check(matches(isDisplayed()));
        onView(withText("ml"))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText("ml"))
                .check(matches(isDisplayed()));
    }
}