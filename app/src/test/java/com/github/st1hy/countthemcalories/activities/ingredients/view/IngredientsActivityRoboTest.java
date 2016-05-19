package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.SelectIngredientTypeActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivityRoboTest;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.JointIngredientTagDao;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static com.github.st1hy.countthemcalories.activities.tags.view.TagsActivityRoboTest.exampleTags;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.getOrZero;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class IngredientsActivityRoboTest {

    public static final IngredientTemplate[] exampleIngredients = new IngredientTemplate[]{
            new IngredientTemplate(1L, "Ingredient 1", Uri.EMPTY, DateTime.now(), AmountUnitType.MASS,
                    getOrZero("20.5")), // kJ / g in database
            new IngredientTemplate(2L, "Ingredient 22", Uri.EMPTY, DateTime.now(), AmountUnitType.VOLUME,
                    getOrZero("6.04")), // kJ / ml in database
    };
    public static final JointIngredientTag[] exampleJoins = new JointIngredientTag[]{
            new JointIngredientTag(1L, exampleTags[0].getId(), exampleIngredients[0].getId()),
            new JointIngredientTag(2L, exampleTags[0].getId(), exampleIngredients[1].getId()),
            new JointIngredientTag(3L, exampleTags[1].getId(), exampleIngredients[1].getId()),
    };
    public static final IngredientTemplate additionalIngredient = new IngredientTemplate(3L, "Ingredient 23",
            Uri.EMPTY, DateTime.now(), AmountUnitType.VOLUME, getOrZero("2.04"));


    private IngredientsActivity activity;
    private DaoSession session;

    private final Timber.Tree tree = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(tag +" " + message);
        }
    };

    @Before
    public void setup() {
        Timber.plant(tree);
        TagsDaoAdapter.debounceTime = 0;
        TestRxPlugins.registerImmediateHookIO();
        session = OverviewActivityRoboTest.prepareDatabase();
        addExampleIngredientsTagsAndJoin(session);
    }

    private void setupActivity() {
        activity = Robolectric.setupActivity(IngredientsActivity.class);
    }

    public static void addExampleIngredientsTagsAndJoin(DaoSession session) {
        IngredientTemplateDao templateDao = session.getIngredientTemplateDao();
        JointIngredientTagDao jointIngredientTagDao = session.getJointIngredientTagDao();
        TagsActivityRoboTest.addExampleTags(session);
        templateDao.deleteAll();
        jointIngredientTagDao.deleteAll();
        templateDao.insertInTx(exampleIngredients);
        jointIngredientTagDao.insertInTx(exampleJoins);
        session.clear();
        templateDao.loadAll();
    }

    @After
    public void tearDown() throws Exception {
        Timber.uproot(tree);
        TestRxPlugins.reset();
        TagsDaoAdapter.debounceTime = 250;
    }

    @Test
    public void testAddIngredient() throws Exception {
        setupActivity();

        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
        activity.fab.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent request = shadowActivity.getNextStartedActivity();
        assertThat(request, hasComponent(new ComponentName(activity, SelectIngredientTypeActivity.class)));

        shadowActivity.receiveResult(request, SelectIngredientTypeActivity.RESULT_DRINK, null);
        request = shadowActivity.getNextStartedActivity();
        assertThat(request, hasComponent(new ComponentName(activity, AddIngredientActivity.class)));
        assertThat(request, hasAction(AddIngredientActivity.ACTION_CREATE_DRINK));

        addAdditionalIngredientToDatabase();
        shadowActivity.receiveResult(request, AddIngredientActivity.RESULT_OK, null);
        activity.adapter.onStop();
        activity.adapter.onStart();

        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));
    }

    private void addAdditionalIngredientToDatabase() {
        session.insert(additionalIngredient);
        Assert.assertThat(session.getIngredientTemplateDao().loadAll(), hasSize(3));
    }

    @Test
    public void testSearch() throws Exception {
        setupActivity();
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));

        activity.searchView.performClick();
        activity.searchView.setQuery("Ingredient 2", true);

        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(2));
    }

    @Test
    public void testSelect() throws Exception {
        setupActivity();
        activity = Robolectric.buildActivity(IngredientsActivity.class)
                .withIntent(new Intent(IngredientsActivity.ACTION_SELECT_INGREDIENT))
                .setup().get();
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
        activity.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_button).performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        assertThat(shadowActivity.isFinishing(), equalTo(true));
        assertThat(shadowActivity.getResultCode(), equalTo(Activity.RESULT_OK));
        IngredientTypeParcel parcel = shadowActivity.getResultIntent()
                .getParcelableExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL);
        IngredientTemplate ingredientTemplate = parcel.getWhenReady().getOrNull();
        assertNotNull(ingredientTemplate);
        assertThat(ingredientTemplate.getId(), equalTo(exampleIngredients[0].getId()));
        assertThat(ingredientTemplate.getName(), equalTo(exampleIngredients[0].getName()));
    }
}