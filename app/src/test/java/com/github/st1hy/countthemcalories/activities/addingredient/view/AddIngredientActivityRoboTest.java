package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.content.Intent;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.presenter.AddIngredientPresenterImp;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowDialog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AddIngredientActivityRoboTest {

    private AddIngredientActivity activity;
    private AddIngredientPresenterImp presenterMock;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(AddIngredientActivity.class);
        presenterMock = Mockito.mock(AddIngredientPresenterImp.class);
        activity.presenter = presenterMock;
    }

    @Test
    public void testActivityStart() {
        assertThat(activity, notNullValue());
        assertThat(activity.presenter, notNullValue());
        assertThat(activity.picasso, notNullValue());
        assertThat(activity.toolbar, notNullValue());
        assertThat(activity.getComponent(), notNullValue());
        assertThat(activity.component, notNullValue());
        assertThat(activity.energyDensityValue, notNullValue());
        assertThat(activity.ingredientImage, notNullValue());
        assertThat(activity.name, notNullValue());
        assertThat(activity.tagsRecycler, notNullValue());
        assertThat(activity.selectUnit, notNullValue());
    }

    @Test
    public void testSaveButtonAction() throws Exception {
        ShadowActivity shadowActivity = shadowOf(activity);
        shadowActivity.onCreateOptionsMenu(new RoboMenu());
        shadowActivity.clickMenuItem(R.id.action_save);

        verify(presenterMock, only()).onClickedOnAction(R.id.action_save);
    }

    @Test
    public void testImageButton() throws Exception {
        activity.ingredientImage.performClick();

        verify(presenterMock, only()).onImageClicked();
    }

    @Test
    public void testOpenIngredientsActivity() throws Exception {
        activity.openIngredientsScreen();
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, IngredientsActivity.class)));
    }

    @Test
    public void testSelectUnit() throws Exception {
        activity.selectUnit.performClick();

        ShadowDialog shadowDialog = shadowOf(RuntimeEnvironment.application).getLatestDialog();
        assertThat(shadowDialog, notNullValue());
    }
}