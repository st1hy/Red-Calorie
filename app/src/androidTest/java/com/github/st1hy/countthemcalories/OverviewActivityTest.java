package com.github.st1hy.countthemcalories;

import android.content.ComponentName;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
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
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.intent.Intents.assertNoUnverifiedIntents;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.st1hy.countthemcalories.matchers.MenuItemMatchers.menuItemIsChecked;
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
        onView(ViewMatchers.withId(R.id.overview_fab)).check(matches(isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), AddMealActivity.class)));
        onView(withId(R.id.add_meal_content)).check(matches(isDisplayed()));
        closeSoftKeyboard();
        pressBack();
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void testOpenSettingsWithMenu() {
        Espresso.openActionBarOverflowOrOptionsMenu(getTargetContext());
        onView(withText(R.string.action_settings))
                .check(matches(isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), SettingsActivity.class)));
    }

    @Test
    public void testNavigateToOverview() throws Exception {
        openDrawerMenu();
        onView(withId(R.id.nav_view))
                .check(matches(isDisplayed()))
                .check(matches(menuItemIsChecked(R.id.nav_overview)))
                .perform(navigateTo(R.id.nav_overview));
        assertNoUnverifiedIntents();
        onView(withId(R.id.overview_content)).check(matches(isDisplayed()));
        synchronized (this) {
            wait(200); //Drawer maybe closing
        }
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
    }

    @Test
    public void testNavigateToIngredients() {
        openDrawerMenu();
        onView(withId(R.id.nav_view))
                .check(matches(isDisplayed()))
                .check(matches(menuItemIsChecked(R.id.nav_overview)))
                .perform(navigateTo(R.id.nav_ingredients));
        onView(withId(R.id.ingredients_content)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        intended(hasComponent(new ComponentName(getTargetContext(), IngredientsActivity.class)));
        pressBack();
        onView(withId(R.id.nav_view))
                .check(matches(menuItemIsChecked(R.id.nav_overview)));
    }

    @Test
    public void testNavigateToSettings() {
        openDrawerMenu();
        onView(withId(R.id.nav_view))
                .check(matches(isDisplayed()))
                .check(matches(menuItemIsChecked(R.id.nav_overview)))
                .perform(navigateTo(R.id.nav_settings));
        onView(withId(R.id.settings_content)).check(matches(isDisplayed()));
        intended(hasComponent(new ComponentName(getTargetContext(), SettingsActivity.class)));
        pressBack();
        onView(withId(R.id.nav_view))
                .check(matches(menuItemIsChecked(R.id.nav_overview)));
    }

    @Test
    public void testNavigateToTags() {
        openDrawerMenu();
        onView(withId(R.id.nav_view))
                .check(matches(isDisplayed()))
                .check(matches(menuItemIsChecked(R.id.nav_overview)))
                .perform(navigateTo(R.id.nav_tags));
        intended(hasComponent(new ComponentName(getTargetContext(), TagsActivity.class)));
        onView(withId(R.id.tags_recycler)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.nav_view))
                .check(matches(menuItemIsChecked(R.id.nav_overview)));
    }

    private void openDrawerMenu() {
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()))
                .perform(open()).check(matches(isOpen()));
    }

}