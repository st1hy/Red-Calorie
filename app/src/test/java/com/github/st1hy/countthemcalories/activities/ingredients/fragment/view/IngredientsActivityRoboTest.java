package com.github.st1hy.countthemcalories.activities.ingredients.fragment.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentModule;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.EditIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.SelectIngredientTypeActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityModule;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsActivityRoboTest;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.JointIngredientTagDao;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.shadows.ShadowSnackbar;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.github.st1hy.countthemcalories.testutils.TimberUtils;
import com.google.common.base.Preconditions;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.List;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsActivityRoboTest.exampleTags;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.getOrZero;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName,
        shadows = {ShadowSnackbar.class})
public class IngredientsActivityRoboTest {

    public static final IngredientTemplate[] exampleIngredients = new IngredientTemplate[]{
            new IngredientTemplate(1L, "Ingredient 1", Uri.EMPTY, DateTime.now(), AmountUnitType.MASS,
                    getOrZero("20.5")), // kJ / g in database
            new IngredientTemplate(2L, "Ingredient 22", Uri.EMPTY, DateTime.now(), AmountUnitType.VOLUME,
                    getOrZero("6.04")), // kJ / ml in database
            new IngredientTemplate(3L, "Ingredient 23", Uri.EMPTY, DateTime.now(), AmountUnitType.VOLUME,
                    getOrZero("7.04")), // kJ / ml in database
    };
    public static final JointIngredientTag[] exampleJoins = new JointIngredientTag[]{
            new JointIngredientTag(1L, exampleTags[0].getId(), exampleIngredients[0].getId()),
            new JointIngredientTag(2L, exampleTags[0].getId(), exampleIngredients[1].getId()),
            new JointIngredientTag(3L, exampleTags[1].getId(), exampleIngredients[1].getId()),
    };
    public static final IngredientTemplate additionalIngredient = new IngredientTemplate(4L, "Ingredient 3",
            Uri.EMPTY, DateTime.now(), AmountUnitType.VOLUME, getOrZero("2.04"));


    private IngredientsActivity activity;
    private IngredientsFragment fragment;
    private DaoSession session;

    @Before
    public void setup() {
        Timber.plant(TimberUtils.ABOVE_WARN);
        IngredientsActivityModule.debounceTime = 0;
        TestRxPlugins.registerImmediateHookIO();
        session = OverviewActivityRoboTest.prepareDatabase();
        addExampleIngredientsTagsAndJoin(session);
    }

    private void setupActivity() {
        activity = Robolectric.setupActivity(IngredientsActivity.class);
        setupFragment();
    }

    private void setupFragment() {
        fragment = (IngredientsFragment) activity.getSupportFragmentManager()
                .findFragmentByTag("ingredients content");
    }

    public static void addExampleIngredientsTagsAndJoin(DaoSession session) {
        IngredientTemplateDao templateDao = session.getIngredientTemplateDao();
        JointIngredientTagDao jointIngredientTagDao = session.getJointIngredientTagDao();

        session.getMealDao().deleteAll();
        session.getIngredientDao().deleteAll();
        templateDao.deleteAll();
        jointIngredientTagDao.deleteAll();

        TagsActivityRoboTest.addExampleTags(session);

        templateDao.insertInTx(exampleIngredients);
        jointIngredientTagDao.insertInTx(exampleJoins);
        session.clear();
        templateDao.loadAll();
    }

    @After
    public void tearDown() throws Exception {
        Timber.uprootAll();
        TestRxPlugins.reset();
        IngredientsActivityModule.debounceTime = 250;
    }

    @Test
    public void testAddIngredient() throws Exception {
        setupActivity();

        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));
        Preconditions.checkNotNull(activity.findViewById(R.id.ingredients_fab)).performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent request = shadowActivity.getNextStartedActivity();
        assertThat(request, hasComponent(new ComponentName(activity, SelectIngredientTypeActivity.class)));

        shadowActivity.receiveResult(request, SelectIngredientTypeActivity.RESULT_DRINK, null);
        request = shadowActivity.getNextStartedActivity();
        assertThat(request, hasComponent(new ComponentName(activity, AddIngredientActivity.class)));
        assertThat(request, hasAction(AddIngredientType.DRINK.getAction()));

        addAdditionalIngredientToDatabase();
        shadowActivity.receiveResult(request, AddIngredientActivity.RESULT_OK, null);
        fragment.adapter.onStop();
        fragment.adapter.onStart();

        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(5));
    }

    private void addAdditionalIngredientToDatabase() {
        session.insert(additionalIngredient);
        assertThat(session.getIngredientTemplateDao().loadAll(), hasSize(4));
    }

    @Test
    public void testSearch() throws Exception {
        setupActivity();
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));

        TokenSearchView searchView = (TokenSearchView) activity.findViewById(R.id.ingredients_search_view);
        Preconditions.checkNotNull(searchView);
        searchView.performClick();
        searchView.setQuery("Ingredient 2");

        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(3));
    }

    @Test
    public void testSelect() throws Exception {
        activity = Robolectric.buildActivity(IngredientsActivity.class)
                .withIntent(new Intent(IngredientsActivity.ACTION_SELECT_INGREDIENT))
                .setup().get();
        setupFragment();
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));
        fragment.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_button)
                .performClick();
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

    @Test
    public void testDelete() throws Exception {
        setupActivity();

        assertThat(session.getIngredientTemplateDao().loadAll(), hasSize(3));
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));
        TextView title = (TextView) fragment.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_name);
        assertThat(title.getText().toString(), equalTo(exampleIngredients[0].getName()));

        fragment.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_delete).performClick();
        assertThat(session.getIngredientTemplateDao().loadAll(), hasSize(2));
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(3));
        title = (TextView) fragment.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_name);
        assertThat(title.getText().toString(), equalTo(exampleIngredients[1].getName()));

        fragment.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_delete).performClick();
        List<IngredientTemplate> templates = session.getIngredientTemplateDao().loadAll();
        assertThat(templates, hasSize(1));
        assertThat(templates.get(0).getName(), equalTo(exampleIngredients[2].getName()));
    }

    @Test
    public void testDeleteWithDialog() throws Exception {
        OverviewActivityRoboTest.addMealsIngredients(session);
        setupActivity();

        assertThat(session.getIngredientTemplateDao().loadAll(), hasSize(3));
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));
        fragment.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_delete).performClick();
        assertThat(session.getIngredientTemplateDao().loadAll(), hasSize(3));
        ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        assertThat(session.getIngredientTemplateDao().loadAll(), hasSize(2));

        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(3));
    }

    @Test
    public void testEdit() throws Exception {
        setupActivity();

        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));
        fragment.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_edit).performClick();
        Intent nextStartedActivity = shadowOf(activity).getNextStartedActivity();
        assertThat(nextStartedActivity, hasComponent(new ComponentName(activity, EditIngredientActivity.class)));
        assertThat(nextStartedActivity, hasExtra(equalTo(AddIngredientFragmentModule.ARG_EDIT_INGREDIENT_PARCEL), notNullValue()));
        IngredientTemplate template = nextStartedActivity.<IngredientTypeParcel>getParcelableExtra(AddIngredientFragmentModule.ARG_EDIT_INGREDIENT_PARCEL)
                .getWhenReady().getOrNull();
        assertNotNull(template);
        assertThat(template.getName(), equalTo(exampleIngredients[0].getName()));
    }


    @Test
    public void testUndoRemove() throws Exception {
        setupActivity();
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));
        testDeleteWithDialog();
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(3));
        ShadowSnackbar.getLatest().performAction();
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));
    }

    @Test
    public void testAddToNewMeal() throws Exception {
        setupActivity();
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));
        fragment.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_button).performClick();
        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(latestAlertDialog);
        shadowOf(latestAlertDialog).clickOnItem(0); //Add to new meal
        Intent nextStartedActivity = shadowOf(activity).getNextStartedActivity();
        assertNotNull(nextStartedActivity);
        assertThat(nextStartedActivity, hasComponent(new ComponentName(activity, AddMealActivity.class)));
        assertThat(nextStartedActivity, hasExtra(
                equalTo(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL),
                any(IngredientTypeParcel.class)
        ));
    }


    @Test
    public void testEditIngredientFromOptions() throws Exception {
        setupActivity();
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));
        fragment.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_button).performClick();
        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(latestAlertDialog);
        shadowOf(latestAlertDialog).clickOnItem(1); //Edit ingredient
        Intent nextStartedActivity = shadowOf(activity).getNextStartedActivity();
        assertNotNull(nextStartedActivity);
        assertThat(nextStartedActivity, hasComponent(
                new ComponentName(activity, EditIngredientActivity.class)));
        assertThat(nextStartedActivity, hasExtra(
                equalTo(AddIngredientFragmentModule.ARG_EDIT_INGREDIENT_PARCEL),
                any(IngredientTypeParcel.class)
        ));
    }

    @Test
    public void testRemoveIngredientFromDialog() throws Exception {
        setupActivity();
        assertThat(session.getIngredientTemplateDao().loadAll(), hasSize(3));
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(4));

        fragment.recyclerView.getChildAt(0).findViewById(R.id.ingredients_item_button).performClick();
        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(latestAlertDialog);
        shadowOf(latestAlertDialog).clickOnItem(2); //Remove ingredient

        assertThat(session.getIngredientTemplateDao().loadAll(), hasSize(2));
        assertThat(fragment.recyclerView.getAdapter().getItemCount(), equalTo(3));
    }
}