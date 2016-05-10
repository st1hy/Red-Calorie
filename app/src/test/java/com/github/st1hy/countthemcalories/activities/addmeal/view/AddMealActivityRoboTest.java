package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsTestActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.shadows.ShadowActivity;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AddMealActivityRoboTest {
    private AddMealActivity activity;
    private final Timber.Tree tree = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(message);
        }
    };

    @Before
    public void setup() {
        Timber.plant(tree);
        TestRxPlugins.registerImmediateHookIO();
        activity = Robolectric.setupActivity(AddMealTestActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        Timber.uproot(tree);
        TestRxPlugins.reset();
    }

    @Test
    public void testSaveButtonAction() throws Exception {
        ShadowActivity shadowActivity = shadowOf(activity);
        shadowActivity.onCreateOptionsMenu(new RoboMenu());
        shadowActivity.clickMenuItem(R.id.action_save);

        Intent resultIntent = shadowActivity.peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, OverviewActivity.class)));
    }

    @Test
    public void testOnSaveInstance() throws Exception {
        activity.presenter = mock(AddMealPresenter.class);
        activity.onSaveInstanceState(new Bundle());
        verify(activity.presenter).onSaveState(any(Bundle.class));
    }

    @Test
    public void testAddIngredient() throws Exception {
        assertThat(activity.ingredientList.getChildCount(), equalTo(0));

        addIngredient();

        assertThat(activity.ingredientList.getChildCount(), equalTo(1));
    }


    private void addIngredient() {
        activity.addIngredientFab.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent requestIntent = shadowActivity.peekNextStartedActivity();
        assertThat(requestIntent, hasComponent(new ComponentName(activity, IngredientsActivity.class)));
        assertThat(requestIntent, hasAction(IngredientsActivity.ACTION_SELECT_INGREDIENT));

        Intent intent = new Intent();
        intent.putExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_ID, IngredientsTestActivity.exampleIngredients[0].getId());
        shadowActivity.receiveResult(requestIntent, Activity.RESULT_OK, intent);
    }
}
