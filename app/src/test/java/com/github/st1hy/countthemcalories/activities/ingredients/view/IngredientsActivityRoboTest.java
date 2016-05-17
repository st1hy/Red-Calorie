package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.SelectIngredientTypeActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsTestComponent;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

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
import static com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsTestActivity.exampleIngredients;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class IngredientsActivityRoboTest {

    private IngredientsActivity activity;

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
    }

    private void setupActivity() {
        activity = Robolectric.setupActivity(IngredientsTestActivity.class);
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
        IngredientsTestComponent component = (IngredientsTestComponent) activity.component;
        component.getDaoSession().insert(IngredientsTestActivity.additionalIngredient);
        Assert.assertThat(component.getDaoSession().getIngredientTemplateDao().loadAll(), hasSize(3));
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
        activity = Robolectric.buildActivity(IngredientsTestActivity.class)
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