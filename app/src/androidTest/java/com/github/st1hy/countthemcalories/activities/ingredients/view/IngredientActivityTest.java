package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.content.ComponentName;
import android.net.Uri;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.SearchView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.SelectIngredientTypeActivity;
import com.github.st1hy.countthemcalories.activities.tags.view.DbProcessingIdleResource;
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
import org.junit.After;
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
import static com.github.st1hy.countthemcalories.activities.tags.view.TagsActivityTest.exampleTags;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.getOrZero;
import static org.hamcrest.Matchers.hasSize;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class IngredientActivityTest {
    public static final IngredientTemplate[] exampleIngredients = new IngredientTemplate[]{
            new IngredientTemplate(1L, "Ingredient 1", Uri.EMPTY, DateTime.now(), AmountUnitType.MASS,
                    getOrZero("20.5")), // kJ / g in database
            new IngredientTemplate(2L, "Ingredient 22", Uri.EMPTY, DateTime.now(), AmountUnitType.VOLUME,
                    getOrZero("6.04")), // kJ / ml in database
    };
    public static final JointIngredientTag[] exampleJoins = new JointIngredientTag[] {
            new JointIngredientTag(1L, exampleTags[0].getId(), exampleIngredients[0].getId()),
            new JointIngredientTag(2L, exampleTags[0].getId(), exampleIngredients[1].getId()),
            new JointIngredientTag(3L, exampleTags[1].getId(), exampleIngredients[1].getId()),
    };

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<IngredientsActivity> main = new IntentsTestRule<>(IngredientsActivity.class);
    private final DbProcessingIdleResource idlingDbProcess = new DbProcessingIdleResource();

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);
    private IngredientTemplateDao templateDao;


    @Before
    public void setUp() throws Exception {
        ApplicationTestComponent component = (ApplicationTestComponent) ((CaloriesCounterApplication) getTargetContext().getApplicationContext()).getComponent();
        DaoSession session = component.getDaoSession();
        addExampleIngredientsTagsAndJoin(session);
        templateDao = session.getIngredientTemplateDao();
        component.getTagsModel().getDbProcessingObservable().subscribe(idlingDbProcess);
        Espresso.registerIdlingResources(idlingDbProcess.getIdlingResource());
    }

    @After
    public void tearDown() throws Exception {
        idlingDbProcess.unsubscribe();
        Espresso.unregisterIdlingResources(idlingDbProcess.getIdlingResource());
    }

    public static void addExampleIngredientsTagsAndJoin(DaoSession session) {
        IngredientTemplateDao templateDao = session.getIngredientTemplateDao();
        JointIngredientTagDao jointIngredientTagDao = session.getJointIngredientTagDao();
        TagsActivityTest.addExampleTags(session);
        templateDao.deleteAll();
        jointIngredientTagDao.deleteAll();
        templateDao.insertInTx(exampleIngredients);
        jointIngredientTagDao.insertInTx(exampleJoins);
        session.clear();
        templateDao.loadAll();
    }

    @Test
    public void testDisplaysIngredients() throws Exception {
        for (IngredientTemplate ingr : exampleIngredients) {
            onView(withText(ingr.getName())).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testSearch() throws InterruptedException {
        onView(withText(exampleIngredients[0].getName())).check(matches(isDisplayed()));
        onView(withText(exampleIngredients[1].getName())).check(matches(isDisplayed()));
        onView(withClassName(Matchers.equalTo(SearchView.class.getName())))
                .check(matches(isDisplayed()))
                .perform(click())
                .perform(typeTextIntoFocusedView("Ingredient 2"));
        synchronized (this) {
            wait(500); //debounce
        }
        onView(withText(exampleIngredients[0].getName())).check(doesNotExist());
        onView(withText(exampleIngredients[1].getName())).check(matches(isDisplayed()));
    }


    @Test
    public void testAddIngredient() throws Exception {
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

        List<IngredientTemplate> ingredientTemplates = templateDao.loadAll();
        assertThat(ingredientTemplates, hasSize(3));
    }
}
