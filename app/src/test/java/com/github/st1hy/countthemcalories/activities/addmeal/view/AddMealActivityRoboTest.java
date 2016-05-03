package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.content.Intent;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenterImp;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.testrunner.RxRobolectricGradleTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.shadows.ShadowActivity;

import rx.plugins.TestRxPlugins;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RxRobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AddMealActivityRoboTest {
    private AddMealActivity activity;
    private AddMealPresenterImp presenterMock;

    @Before
    public void setup() {
        TestRxPlugins.registerImmediateHook();
        activity = Robolectric.setupActivity(AddMealActivity.class);
        presenterMock = Mockito.mock(AddMealPresenterImp.class);
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
        activity.mealImage.performClick();

        verify(presenterMock, only()).onImageClicked();
    }

    @Test
    public void testOpenOverviewActivity() throws Exception {
        activity.openOverviewActivity();
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, equalTo(new Intent(activity, OverviewActivity.class)));
    }

}
