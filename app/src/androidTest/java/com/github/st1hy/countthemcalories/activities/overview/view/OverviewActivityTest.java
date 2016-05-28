package com.github.st1hy.countthemcalories.activities.overview.view;

import android.content.ComponentName;
import android.net.Uri;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientActivityTest;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientDao;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.MealDao;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.intent.Intents.assertNoUnverifiedIntents;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.st1hy.countthemcalories.actions.CTCViewActions.loopMainThreadForAtLeast;
import static com.github.st1hy.countthemcalories.matchers.MenuItemMatchers.menuItemIsChecked;
import static org.hamcrest.Matchers.hasSize;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class OverviewActivityTest {
    public static final Meal[] exampleMeals = new Meal[]{
            new Meal(1L, "Meal 1", Uri.EMPTY, DateTime.now()),
            new Meal(2L, "Meal 2", Uri.EMPTY, DateTime.now()),
    };
    public static final Ingredient[] exampleIngredients = new Ingredient[] {
            new Ingredient(1L, new BigDecimal("30"), exampleMeals[0].getId(), IngredientActivityTest.exampleIngredients[0].getId()),
            new Ingredient(2L, new BigDecimal("20"), exampleMeals[0].getId(), IngredientActivityTest.exampleIngredients[1].getId()),
            new Ingredient(3L, new BigDecimal("70"), exampleMeals[1].getId(), IngredientActivityTest.exampleIngredients[1].getId()),
    };

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<OverviewActivity> main = new IntentsTestRule<>(OverviewActivity.class, true, false);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    @Before
    public void setUp() throws Exception {
        ApplicationTestComponent component = (ApplicationTestComponent) ((CaloriesCounterApplication) getTargetContext().getApplicationContext()).getComponent();
        DaoSession session = component.getDaoSession();
        addExampleMealsIngredientsTags(session);
        main.launchActivity(null);
    }

    public static void addExampleMealsIngredientsTags(DaoSession session) {
        IngredientActivityTest.addExampleIngredientsTagsAndJoin(session);
        IngredientDao ingredientDao = session.getIngredientDao();
        MealDao mealDao = session.getMealDao();
        ingredientDao.deleteAll();
        mealDao.deleteAll();

        mealDao.insertInTx(exampleMeals);
        ingredientDao.insertInTx(exampleIngredients);
        session.clear();
        ingredientDao.loadAll();
        List<Meal> meals = mealDao.loadAll();
        assertThat(meals, hasSize(2));
        assertThat(exampleMeals[0].getIngredients(), hasSize(2));
        assertThat(exampleMeals[1].getIngredients(), hasSize(1));
    }

    @Test
    public void testDisplayMeals() throws Exception {
        onView(withText(exampleMeals[0].getName())).check(matches(isDisplayed()));
        onView(withText(exampleMeals[1].getName())).check(matches(isDisplayed()));
    }

    @Test
    public void testCanAddNewMeal() {
        onView(ViewMatchers.withId(R.id.overview_fab)).check(matches(isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), AddMealActivity.class)));
        onView(withId(R.id.add_meal_content)).check(matches(isDisplayed()));
        onView(withHint(R.string.add_meal_name_hint)).perform(typeTextIntoFocusedView("Meal 3"));
        closeSoftKeyboard();
        onView(withId(R.id.add_meal_fab_add_ingredient)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), IngredientsActivity.class)));
        onView(withId(R.id.ingredients_content)).check(matches(isDisplayed()));
        onView(withText(IngredientActivityTest.exampleIngredients[0].getName())).check(matches(isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), IngredientDetailsActivity.class)));
        onView(withText(IngredientActivityTest.exampleIngredients[0].getName())).check(matches(isDisplayed()));
        onView(withHint(R.string.add_meal_ingredient_amount_hint)).perform(click())
                .perform(typeTextIntoFocusedView("200"));
        onView(withId(R.id.add_meal_ingredient_accept)).perform(click());
        onView(withText(R.string.add_meal_save_action)).perform(click());
        onView(ViewMatchers.withId(R.id.overview_fab)).check(matches(isDisplayed()));
        onView(withText("Meal 3"))
                .check(matches(isDisplayed()));
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
        onView(withId(R.id.overview_recycler_view))
                .check(matches(isDisplayed()))
                .perform(loopMainThreadForAtLeast(200)); //Drawer may be closing
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

    @Test
    public void testEditMeal() throws Exception {
        onView(withText(exampleMeals[0].getName())).check(matches(isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), MealDetailActivity.class)));
        onView(withText(exampleMeals[0].getName())).check(matches(isDisplayed()));
        onView(withId(R.id.overview_extended_edit)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), AddMealActivity.class)));
        onView(withText(exampleMeals[0].getName()))
                .check(matches(isDisplayed()))
                .perform(clearText());
        onView(withHint(R.string.add_meal_name_hint))
                .perform(typeTextIntoFocusedView("Meal edited"));
        onView(withText(R.string.add_meal_save_action)).perform(click());
        onView(withId(R.id.overview_recycler_view)).check(matches(isDisplayed()));
        onView(withText("Meal edited")).check(matches(isDisplayed()));
    }
}