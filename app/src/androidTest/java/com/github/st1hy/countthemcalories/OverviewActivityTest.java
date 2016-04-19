package com.github.st1hy.countthemcalories;

import android.content.ComponentName;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
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
import static com.github.st1hy.countthemcalories.matchers.MenuItemMatchers.menuItemIsChecked;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class OverviewActivityTest {

    @Rule
    public final IntentsTestRule<OverviewActivity> main = new IntentsTestRule<>(OverviewActivity.class);

    @Test
    public void testActivityStart() {
        assertThat(main.getActivity(), notNullValue());
    }

    /**
     * Navigation drawer tests are flaky because of animation.
     */
    @Test
    public void testNavigateToOverview() {
        openDrawerMenu();
        onView(withId(R.id.nav_view))
                .check(matches(isDisplayed()))
                .check(matches(menuItemIsChecked(R.id.nav_overview)))
                .perform(navigateTo(R.id.nav_overview))
                .check(matches(menuItemIsChecked(R.id.nav_overview)));
        assertNoUnverifiedIntents();
        onView(withId(R.id.overview_content)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
    }

    /**
     * Navigation drawer tests are flaky because of animation.
     */
    @Test
    public void testNavigateToIngredients() {
        openDrawerMenu();
        onView(withId(R.id.nav_view))
                .check(matches(isDisplayed()))
                .check(matches(menuItemIsChecked(R.id.nav_overview)))
                .perform(navigateTo(R.id.nav_ingredients))
                .check(matches(menuItemIsChecked(R.id.nav_ingredients)));
        onView(withId(R.id.ingredient_content)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        intended(hasComponent(new ComponentName(getTargetContext(), IngredientsActivity.class)));
        pressBack();
        openDrawerMenu();
        onView(withId(R.id.nav_view))
                .check(matches(menuItemIsChecked(R.id.nav_overview)));
    }

    private void openDrawerMenu() {
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()))
                .perform(open()).check(matches(isOpen()));
    }

    @Test
    public void testCanAddNewMeal() {
        onView(withId(R.id.overview_fab)).check(matches(isDisplayed()))
                .perform(ViewActions.click());
        intended(hasComponent(new ComponentName(getTargetContext(), AddMealActivity.class)));
        onView(withId(R.id.add_meal_content)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.overview_toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void testDisplayTotalCalories() {
//        onView(withId(R.id.overview_toolbar)).inRoot(with)
    }
}