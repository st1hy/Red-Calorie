package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.content.ComponentName;
import android.net.Uri;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.SearchView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.SelectIngredientTypeActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivityTest;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivityTest;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.JointIngredientTagDao;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.st1hy.countthemcalories.actions.CTCViewActions.loopMainThreadForAtLeast;
import static com.github.st1hy.countthemcalories.activities.tags.view.TagsActivityTest.exampleTags;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.getOrZero;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class IngredientActivityTest {
    public static final IngredientTemplate[] exampleIngredients = new IngredientTemplate[]{
            new IngredientTemplate(1L, "Ingredient 1", Uri.EMPTY, DateTime.now(), AmountUnitType.MASS,
                    getOrZero("20.5")), // kJ / g in database
            new IngredientTemplate(2L, "Ingredient 22", Uri.EMPTY, DateTime.now(), AmountUnitType.VOLUME,
                    getOrZero("6.04")), // kJ / ml in database
            new IngredientTemplate(3L, "Ingredient 23", Uri.EMPTY, DateTime.now(), AmountUnitType.VOLUME,
                    getOrZero("7.04")), // kJ / ml in database
    };
    public static final JointIngredientTag[] exampleJoins = new JointIngredientTag[] {
            new JointIngredientTag(1L, exampleTags[0].getId(), exampleIngredients[0].getId()),
            new JointIngredientTag(2L, exampleTags[0].getId(), exampleIngredients[1].getId()),
            new JointIngredientTag(3L, exampleTags[1].getId(), exampleIngredients[1].getId()),
    };

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<IngredientsActivity> main = new IntentsTestRule<>(IngredientsActivity.class, true, false);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);
    private DaoSession session;


    @Before
    public void setUp() throws Exception {
        ApplicationTestComponent component = (ApplicationTestComponent) ((CaloriesCounterApplication) getTargetContext().getApplicationContext()).getComponent();
        session = component.getDaoSession();
        addExampleIngredientsTagsAndJoin(session);
        session.getIngredientTemplateDao();
    }

    public static void addExampleIngredientsTagsAndJoin(DaoSession session) {
        IngredientTemplateDao templateDao = session.getIngredientTemplateDao();
        JointIngredientTagDao jointIngredientTagDao = session.getJointIngredientTagDao();

        session.getMealDao().deleteAll();
        session.getIngredientDao().deleteAll();
        templateDao.deleteAll();
        jointIngredientTagDao.deleteAll();

        TagsActivityTest.addExampleTags(session);
        templateDao.insertInTx(exampleIngredients);
        jointIngredientTagDao.insertInTx(exampleJoins);

        session.clear();
        templateDao.loadAll();
    }

    @Test
    public void testDisplaysIngredients() throws Exception {
        main.launchActivity(null);
        for (IngredientTemplate ingr : exampleIngredients) {
            onView(withText(ingr.getName())).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testSearch() throws InterruptedException {
        main.launchActivity(null);
        onView(withText(exampleIngredients[0].getName())).check(matches(isDisplayed()));
        onView(withText(exampleIngredients[1].getName())).check(matches(isDisplayed()));
        onView(withClassName(Matchers.equalTo(SearchView.class.getName())))
                .check(matches(isDisplayed()))
                .perform(click())
                .perform(typeTextIntoFocusedView("Ingredient 2"))
                .perform(loopMainThreadForAtLeast(500)); //Debounce
        onView(withText(exampleIngredients[0].getName())).check(doesNotExist());
        onView(withText(exampleIngredients[1].getName())).check(matches(isDisplayed()));
    }


    @Test
    public void testAddIngredient() throws Exception {
        main.launchActivity(null);
        onView(withId(R.id.ingredients_fab)).check(matches(isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), SelectIngredientTypeActivity.class)));
        onView(withId(R.id.select_ingredient_type_meal)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), AddIngredientActivity.class)));
        onView(withHint(R.string.add_ingredient_name_hint)).check(matches(isDisplayed()))
                .perform(typeTextIntoFocusedView("New ingredient name"));
        onView(withHint(R.string.add_ingredient_energy_density_hint)).check(matches(isDisplayed()))
                .perform(click())
                .perform(typeTextIntoFocusedView("30"));
        onView(withText(R.string.add_ingredient_save_action)).check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.ingredients_content)).check(matches(isDisplayed()));
        onView(withText("New ingredient name")).check(matches(isDisplayed()));

        List<IngredientTemplate> ingredientTemplates = session.getIngredientTemplateDao().loadAll();
        assertThat(ingredientTemplates, hasSize(4));
    }

    @Test
    public void testDelete() throws Exception {
        main.launchActivity(null);
        onView(withText(exampleIngredients[0].getName()))
                .perform(swipeRight());
        onView(allOf(withId(R.id.ingredients_item_delete), isDisplayed()))
                .perform(click());

        onView(withText(exampleIngredients[0].getName()))
                .check(doesNotExist());
        List<IngredientTemplate> ingredientTemplates = session.getIngredientTemplateDao().loadAll();
        assertThat(ingredientTemplates, hasSize(2));

        onView(withText(exampleIngredients[1].getName()))
                .perform(swipeRight());
        onView(allOf(withId(R.id.ingredients_item_delete), isDisplayed()))
                .perform(click());

        onView(withText(exampleIngredients[1].getName()))
                .check(doesNotExist());
        ingredientTemplates = session.getIngredientTemplateDao().loadAll();
        assertThat(ingredientTemplates, hasSize(1));

    }

    @Test
    public void testUndo() throws Exception {
        main.launchActivity(null);
        onView(withText(exampleIngredients[0].getName()))
                .perform(swipeRight());
        onView(allOf(withId(R.id.ingredients_item_delete), isDisplayed()))
                .perform(click());

        onView(withText(exampleIngredients[0].getName()))
                .check(doesNotExist());
        List<IngredientTemplate> ingredientTemplates = session.getIngredientTemplateDao().loadAll();
        assertThat(ingredientTemplates, hasSize(2));

        onView(withText(R.string.undo)).perform(click());
        onView(withText(exampleIngredients[1].getName()))
                .check(matches(isDisplayed()));

        ingredientTemplates = session.getIngredientTemplateDao().loadAll();
        assertThat(ingredientTemplates, hasSize(3));

    }

    @Test
    public void testDeleteWithMeal() throws Exception {
        OverviewActivityTest.addExampleMealsIngredientsTags(session);
        main.launchActivity(null);
        onView(withText(exampleIngredients[0].getName()))
                .perform(swipeRight());
        onView(allOf(withId(R.id.ingredients_item_delete), isDisplayed()))
                .perform(click());

        onView(withText(R.string.ingredients_remove_ingredient_dialog_title)).check(matches(isDisplayed()));
        onView(withText(android.R.string.yes)).perform(click());
        onView(withText(R.string.ingredients_remove_ingredient_dialog_title)).check(doesNotExist());
        onView(withText(exampleIngredients[0].getName()))
                .check(doesNotExist());
        List<IngredientTemplate> ingredientTemplates = session.getIngredientTemplateDao().loadAll();
        assertThat(ingredientTemplates, hasSize(2));
    }

    @Test
    public void testEditIngredient() throws Exception {
        main.launchActivity(null);
        onView(withText(exampleIngredients[0].getName()))
                .perform(swipeLeft());

        onView(allOf(withId(R.id.ingredients_item_edit), isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), AddIngredientActivity.class)));

    }
}
