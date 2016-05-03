package com.github.st1hy.countthemcalories.activities.tags.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.widget.EditText;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsPresenter;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TagsActivityRoboTest {

    private TagsActivity activity;

    @Before
    public void setup() {
        Intent intent = new Intent(TagsTestActivity.ACTION_PICK_TAG);
        activity = Robolectric.buildActivity(TagsTestActivity.class)
                .withIntent(intent)
                .setup()
                .get();
    }

    @Test
    public void testActivityStart() {
        assertThat(activity, notNullValue());
        assertThat(activity.presenter, notNullValue());
        assertThat(activity.toolbar, notNullValue());
        assertThat(activity.getComponent(), notNullValue());
        assertThat(activity.component, notNullValue());
    }

    @Test
    public void testAddNewItem() throws Exception {
        activity.fab.performClick();
        final String testName = "test name";
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
        ShadowAlertDialog shadowAlertDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        assertThat(shadowAlertDialog, notNullValue());
        EditText ediText = (EditText) shadowAlertDialog.getView().findViewById(R.id.tags_dialog_name);
        ediText.setText(testName);
        assertThat(activity.getString(R.string.tags_new_tag_dialog), equalTo(shadowAlertDialog.getTitle()));
        ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));
    }

    @Test
    public void testStart() throws Exception {
        TagsPresenter presenterMock = Mockito.mock(TagsPresenter.class);
        activity.presenter = presenterMock;
        activity.onStart();
        verify(presenterMock).onStart();
    }

    @Test
    public void testOnStop() throws Exception {
        TagsPresenter presenterMock = Mockito.mock(TagsPresenter.class);
        activity.presenter = presenterMock;
        activity.onStop();
        verify(presenterMock).onStop();
    }

    @Test
    public void testRemoveItem() throws Exception {
        activity.recyclerView.getChildAt(0).performLongClick();

        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
        ShadowAlertDialog shadowAlertDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        assertThat(shadowAlertDialog, notNullValue());
        ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(2));
    }


    @Test
    public void testSearch() throws Exception {
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));

        activity.searchView.performClick();
        activity.searchView.setQuery("Tag", true);

        synchronized (this) {
            wait(600); //Debounce on query
        }
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(2));
    }

    @Test
    public void testSelectTag() throws Exception {
        activity.recyclerView.getChildAt(0).performClick();
        assertTrue(shadowOf(activity).isFinishing());
        Intent resultIntent = shadowOf(activity).getResultIntent();
        assertThat(resultIntent, CoreMatchers.notNullValue());
        assertThat(resultIntent.getLongExtra(TagsActivity.EXTRA_TAG_ID, -1L), not(equalTo(-1L)));
    }
}