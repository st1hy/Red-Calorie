package com.github.st1hy.countthemcalories.activities.overview.view;

import android.content.ComponentName;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TimePicker;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.EditMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientActivityTest;
import com.github.st1hy.countthemcalories.activities.mealdetail.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.settings.SettingsActivity;
import com.github.st1hy.countthemcalories.activities.tags.TagsActivity;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientDao;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.MealDao;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.hamcrest.Matcher;
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
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.intent.Intents.assertNoUnverifiedIntents;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.st1hy.countthemcalories.actions.CTCViewActions.loopMainThreadForAtLeast;
import static com.github.st1hy.countthemcalories.actions.CTCViewActions.setTime;
import static com.github.st1hy.countthemcalories.matchers.MenuItemMatchers.menuItemIsChecked;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class OverviewActivityTest {
    public static final Meal[] exampleMeals = new Meal[]{
            new Meal(1L, "Meal 1", Uri.EMPTY, DateTime.now().withTime(15,30,0,0)),
            new Meal(2L, "Meal 2", Uri.EMPTY, DateTime.now().withTime(10, 59, 0, 0)),
    };
    public static final Ingredient[] exampleIngredients = new Ingredient[]{
            new Ingredient(
                    1L,
                    new BigDecimal("30"),
                    exampleMeals[0].getId(),
                    IngredientActivityTest.exampleIngredients[0].getId()
            ),
            new Ingredient(
                    2L,
                    new BigDecimal("20"),
                    exampleMeals[0].getId(),
                    IngredientActivityTest.exampleIngredients[1].getId()
            ),
            new Ingredient(
                    3L,
                    new BigDecimal("70"),
                    exampleMeals[1].getId(),
                    IngredientActivityTest.exampleIngredients[1].getId()
            ),
    };

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<OverviewActivity> main = new IntentsTestRule<>(OverviewActivity.class, true, false);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    @Before
    public void setUp() throws Exception {
        CaloriesCounterApplication application = (CaloriesCounterApplication) getTargetContext()
                .getApplicationContext();
        ApplicationTestComponent component = (ApplicationTestComponent) (application).getComponent();
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
        session.getWeightDao().deleteAll();

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
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.overview_fab_add_meal)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), AddMealActivity.class)));
        onView(withId(R.id.add_meal_content)).check(matches(isDisplayed()));
        onView(withId(R.id.add_meal_name)).perform(typeTextIntoFocusedView("Meal 3"));
        closeSoftKeyboard();
        onView(withId(R.id.add_meal_fab_add_ingredient)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), IngredientsActivity.class)));
        onView(allOf(withId(R.id.ingredients_content), isAssignableFrom(RecyclerView.class)))
                .check(matches(isDisplayed()));
        onView(withText(IngredientActivityTest.exampleIngredients[0].getName())).check(matches(isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), IngredientDetailActivity.class)));
        onView(withText(IngredientActivityTest.exampleIngredients[0].getName())).check(matches(isDisplayed()));
        onView(withHint(R.string.add_meal_ingredient_amount_hint)).perform(click())
                .perform(typeTextIntoFocusedView("200"))
                .perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.add_meal_ingredient_accept)).perform(click());
        onView(withText(R.string.add_meal_save_action)).perform(click());
        onView(ViewMatchers.withId(R.id.overview_fab_add_meal)).check(matches(isDisplayed()));
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
        onView(mainScreen())
                .check(matches(isDisplayed()))
                .perform(loopMainThreadForAtLeast(200)); //Drawer may be closing
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
    }

    @NonNull
    public static Matcher<View> mainScreen() {
        return allOf(withId(R.id.overview_recycler_view), isDisplayed());
    }


    @Test
    public void testNavigateToIngredients() {
        openDrawerMenu();
        onView(withId(R.id.nav_view))
                .check(matches(isDisplayed()))
                .check(matches(menuItemIsChecked(R.id.nav_overview)))
                .perform(navigateTo(R.id.nav_ingredients));
        onView(allOf(withId(R.id.ingredients_content),isAssignableFrom(RecyclerView.class)))
                .check(matches(isDisplayed()));
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
        onView(allOf(withId(R.id.tags_recycler), isAssignableFrom(RecyclerView.class))).check(matches(isDisplayed()));
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
        onView(withId(R.id.meal_detail_edit)).perform(click());
        checkEditActivity();
    }

    @Test
    public void testEditWithScroll() throws Exception {
        onView(mealButtonWithName(exampleMeals[0].getName()))
                .check(matches(isDisplayed()))
                .perform(swipeLeft());

        onView(allOf(withId(R.id.overview_item_edit), isDisplayed())).perform(click());

        checkEditActivityShort();
    }

    private void checkEditActivityShort() {
        intended(hasComponent(new ComponentName(getTargetContext(), EditMealActivity.class)));

        onView(withText(exampleMeals[0].getName()))
                .check(matches(isDisplayed()))
                .perform(clearText());
        onView(withId(R.id.add_meal_name))
                .perform(typeTextIntoFocusedView("Meal edited"));
        onView(withText(R.string.add_meal_save_action)).perform(click());
        onView(mainScreen()).check(matches(isDisplayed()));
        onView(withText("Meal edited")).check(matches(isDisplayed()));
    }

    private void checkEditActivity() {
        intended(hasComponent(new ComponentName(getTargetContext(), EditMealActivity.class)));

        onView(withText(exampleMeals[0].getName()))
                .check(matches(isDisplayed()))
                .perform(clearText());
        onView(withId(R.id.add_meal_name))
                .perform(typeTextIntoFocusedView("Meal edited"));
        onView(withId(R.id.add_meal_time_value)).perform(click());
        onView(withClassName(equalTo(TimePicker.class.getName()))).perform(setTime(20,43));
        onView(withText("OK")).perform(click());

        onView(withText("Ingredient 1")).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.add_meal_ingredient_remove)).perform(click());
        onView(withText("Ingredient 1")).check(doesNotExist());

        onView(withText(R.string.add_meal_save_action)).perform(click());
        onView(mainScreen()).check(matches(isDisplayed()));
        onView(withText("Meal edited")).check(matches(isDisplayed()));
        onView(withText(startsWith("20:43"))).check(matches(isDisplayed()));
        onView(withText(containsString("Ingredient 1"))).check(doesNotExist());
    }

    @Test
    public void testDeleteSwipe() throws Exception {
        onView(mealButtonWithName(exampleMeals[0].getName()))
                .check(matches(isDisplayed()))
                .perform(swipeRight());
        onView(allOf(withId(R.id.overview_item_delete), isDisplayed())).perform(click());
        onView(mealButtonWithName(exampleMeals[0].getName()))
                .check(doesNotExist());
    }

    @NonNull
    public static Matcher<View> mealButtonWithName(String name) {
        return allOf(withId(R.id.overview_item_content),
                withChild(withChild(withChild(withChild(withText(name))))));
    }

    @Test
    public void testDeleteUndo() throws Exception {
        testDeleteSwipe();
        onView(withText(R.string.undo)).perform(click());
        onView(mealButtonWithName(exampleMeals[0].getName()))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAddWeight() throws Exception {
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.overview_fab_add_weight)).perform(click());
        onView(withHint(R.string.add_weight_dialog_hint))
                .perform(typeTextIntoFocusedView("93"));
        onView(withText(android.R.string.ok)).perform(click());
        onView(withId(R.id.fab_expand_menu_button))
                .perform(click());
        onView(withId(R.id.overview_fab_add_weight)).perform(click());
        onView(withHint(R.string.add_weight_dialog_hint))
                .check(matches(withText("93")));

    }
}