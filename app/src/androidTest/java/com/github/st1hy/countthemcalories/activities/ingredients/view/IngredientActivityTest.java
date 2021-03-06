package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.content.ComponentName;
import android.net.Uri;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.EditIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivityTest;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivityTest;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.JointIngredientTagDao;
import com.github.st1hy.countthemcalories.database.property.CreationSource;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.st1hy.countthemcalories.actions.CTCViewActions.loopMainThreadForAtLeast;
import static com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity.ARG_EDIT_INGREDIENT_PARCEL;
import static com.github.st1hy.countthemcalories.activities.tags.view.TagsActivityTest.exampleTags;
import static com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchViewActions.clickExpand;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.getOrZero;
import static com.github.st1hy.countthemcalories.matchers.EditTextMatchers.hasNoError;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class IngredientActivityTest {
    public static final IngredientTemplate[] exampleIngredients = new IngredientTemplate[]{
            new IngredientTemplate(1L, "Ingredient 1", Uri.EMPTY, CreationSource.USER, AmountUnitType.MASS,
                    getOrZero("20.5")), // kJ / g in database
            new IngredientTemplate(2L, "Ingredient 22", Uri.EMPTY, CreationSource.USER, AmountUnitType.VOLUME,
                    getOrZero("6.04")), // kJ / ml in database
            new IngredientTemplate(3L, "Ingredient 23", Uri.EMPTY, CreationSource.USER, AmountUnitType.VOLUME,
                    getOrZero("7.04")), // kJ / ml in database
    };
    public static final JointIngredientTag[] exampleJoins = new JointIngredientTag[]{
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
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        for (IngredientTemplate ingr : exampleIngredients) {
            onView(withText(ingr.getName())).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testSearch() throws InterruptedException {
        main.launchActivity(null);
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withText(exampleIngredients[0].getName())).check(matches(isDisplayed()));
        onView(withText(exampleIngredients[1].getName())).check(matches(isDisplayed()));
        onView(withId(R.id.ingredients_search_view))
                .check(matches(isDisplayed()))
                .perform(clickExpand());
        onView(withId(R.id.token_search_text_view))
                .check(matches(isDisplayed()))
                .perform(typeTextIntoFocusedView("Ingredient 2"))
                .perform(loopMainThreadForAtLeast(500)); //Debounce
        onView(withText(exampleIngredients[0].getName())).check(doesNotExist());
        onView(withText(exampleIngredients[1].getName())).check(matches(isDisplayed()));
    }


    @Test
    public void testSearchUsingCategory() throws Exception {
        main.launchActivity(null);
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withText(exampleIngredients[0].getName())).check(matches(isDisplayed()));
        onView(withText(exampleIngredients[1].getName())).check(matches(isDisplayed()));
        onView(withId(R.id.ingredients_search_view))
                .check(matches(isDisplayed()))
                .perform(clickExpand());
        onView(withId(R.id.token_search_text_view))
                .check(matches(isDisplayed()))
                .perform(typeTextIntoFocusedView("Tag"))
                .perform(loopMainThreadForAtLeast(500)); //Debounce

        onData(allOf(is(instanceOf(String.class)), is("Tag 2")))
                .inRoot(RootMatchers.withDecorView(not(is(main
                        .getActivity().getWindow().getDecorView()))))
                .perform(click());
        onView(allOf(withId(R.id.ingredients_content),isAssignableFrom(RecyclerView.class)))
                .perform(loopMainThreadForAtLeast(500));
        onView(withText(exampleIngredients[0].getName())).check(doesNotExist());
        onView(withText(exampleIngredients[1].getName())).check(matches(isDisplayed()));
        onView(withText(exampleIngredients[2].getName())).check(doesNotExist());
    }


    @Test
    public void testAddIngredient() throws Exception {
        main.launchActivity(null);
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withId(R.id.ingredients_fab)).check(matches(isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), AddIngredientActivity.class)));
        onView(withText(R.string.add_ingredient_save_action)).check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.add_ingredient_name)).check(matches(hasErrorText(getTargetContext().getString(R.string.add_ingredient_name_error_empty))));
        onView(withHint(R.string.add_ingredient_name_hint)).check(matches(isDisplayed()))
                .perform(typeTextIntoFocusedView("New ingredient name"));
        onView(withId(R.id.add_ingredient_name)).check(matches(hasNoError()));
        onView(withHint(R.string.add_ingredient_energy_density_hint)).check(matches(isDisplayed()))
                .perform(click())
                .perform(typeTextIntoFocusedView("30"));
        onView(withText(R.string.add_ingredient_save_action)).check(matches(isDisplayed()))
                .perform(click());
        onView(allOf(withId(R.id.ingredients_content),isAssignableFrom(RecyclerView.class)))
                .check(matches(isDisplayed()));
        onView(withText("New ingredient name")).check(matches(isDisplayed()));

        List<IngredientTemplate> ingredientTemplates = session.getIngredientTemplateDao().loadAll();
        assertThat(ingredientTemplates, hasSize(4));
    }

    @Test
    public void testDelete() throws Exception {
        main.launchActivity(null);
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withText(exampleIngredients[0].getName()))
                .perform(swipeRight())
                .perform(loopMainThreadForAtLeast(400));
        onView(allOf(withId(R.id.ingredients_item_delete), isDisplayed()))
                .perform(click());

        onView(withText(exampleIngredients[0].getName()))
                .check(doesNotExist());
        List<IngredientTemplate> ingredientTemplates = session.getIngredientTemplateDao().loadAll();
        assertThat(ingredientTemplates, hasSize(2));

        onView(withText(exampleIngredients[1].getName()))
                .perform(swipeRight())
                .perform(loopMainThreadForAtLeast(400));
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
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withText(exampleIngredients[0].getName()))
                .perform(swipeRight())
                .perform(loopMainThreadForAtLeast(400));
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
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withText(exampleIngredients[0].getName()))
                .perform(swipeRight())
                .perform(loopMainThreadForAtLeast(400));
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
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withText(exampleIngredients[0].getName()))
                .perform(swipeLeft())
                .perform(loopMainThreadForAtLeast(400));
        onView(allOf(withId(R.id.ingredients_item_edit), isDisplayed()))
                .perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), EditIngredientActivity.class)));
    }

    @Test
    public void testAddToNewMeal() throws Exception {
        main.launchActivity(null);
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withText(exampleIngredients[0].getName()))
                .perform(click());
        onView(withText(R.string.ingredients_item_add_to_new_meal))
                .perform(click());
        intended(allOf(hasComponent(new ComponentName(getTargetContext(), AddMealActivity.class)),
                hasExtraWithKey(equalTo(AddMealActivity.EXTRA_INGREDIENT_PARCEL))
        ));
    }

    @Test
    public void testEditIngredientFromOptions() throws Exception {
        main.launchActivity(null);
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withText(exampleIngredients[0].getName()))
                .perform(click());
        onView(withText(R.string.ingredients_item_edit_ingredient))
                .perform(click());

        intended(allOf(hasComponent(new ComponentName(getTargetContext(), EditIngredientActivity.class)),
                hasExtraWithKey(equalTo(ARG_EDIT_INGREDIENT_PARCEL))
        ));
    }

    @Test
    public void testDeleteFromOptions() throws Exception {
        main.launchActivity(null);
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withText(exampleIngredients[0].getName()))
                .perform(click());
        onView(withText(R.string.ingredients_item_delete_ingredient))
                .perform(click());
        onView(withText(exampleIngredients[0].getName())).check(doesNotExist());
    }

    @Test
    public void testSearchQueryAddedToNewIngredient() throws Exception {
        final String extraName = "Example ingredient name";
        main.launchActivity(null);
        onView(withId(R.id.token_search_text_view)).perform(loopMainThreadForAtLeast(200));
        onView(withId(R.id.ingredients_search_view))
                .check(matches(isDisplayed()))
                .perform(clickExpand());
        onView(withId(R.id.token_search_text_view))
                .check(matches(isDisplayed()))
                .perform(typeTextIntoFocusedView(extraName))
                .perform(loopMainThreadForAtLeast(500)); //Debounce
        onView(withId(R.id.ingredients_empty)).check(matches(isDisplayed()));
        onView(withId(R.id.ingredients_fab)).perform(click());
        intended(allOf(
                hasComponent(new ComponentName(main.getActivity(), AddIngredientActivity.class)),
                hasExtra(AddIngredientActivity.ARG_EXTRA_NAME, extraName)
        ));
        onView(allOf(withId(R.id.add_ingredient_name), withText(extraName))).check(matches(isDisplayed()));
    }
}
