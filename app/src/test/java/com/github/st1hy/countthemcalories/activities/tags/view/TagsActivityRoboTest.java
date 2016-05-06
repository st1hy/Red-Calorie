package com.github.st1hy.countthemcalories.activities.tags.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsPresenter;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsPresenterImp;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import rx.plugins.TestRxPlugins;
import timber.log.Timber;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@LargeTest
public class TagsActivityRoboTest {

    private  TagsActivity activity;
    private final Timber.Tree tree = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(tag +" " + message);
        }
    };

    @Before
    public void setup() throws Exception {
        Timber.plant(tree);
        TagsPresenterImp.debounceTime = 0;
        TestRxPlugins.registerImmediateHookIO();
        Intent intent = new Intent(TagsTestActivity.ACTION_PICK_TAG);
        activity = Robolectric.buildActivity(TagsTestActivity.class)
                .withIntent(intent)
                .setup()
                .get();
    }

    @After
    public void tearDown() throws Exception {
        Timber.uproot(tree);
        TestRxPlugins.reset();
        TagsPresenterImp.debounceTime = 250;
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
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));
        fillAddNewItemDialog();
        ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        assertFalse(ShadowAlertDialog.getLatestAlertDialog().isShowing());
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(5));
    }

    @Test
    public void testAddNewItemFinishWithEnter() throws Exception {
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));
        fillAddNewItemDialog();
        ShadowAlertDialog shadowAlertDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        EditText ediText = (EditText) shadowAlertDialog.getView().findViewById(R.id.tags_dialog_name);
        ediText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
        assertFalse(ShadowAlertDialog.getLatestAlertDialog().isShowing());
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(5));
    }

    @Test
    public void testAddNewItemFinishWitImeDone() throws Exception {
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));
        fillAddNewItemDialog();
        ShadowAlertDialog shadowAlertDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        EditText ediText = (EditText) shadowAlertDialog.getView().findViewById(R.id.tags_dialog_name);
        ediText.onEditorAction(EditorInfo.IME_ACTION_DONE);
        assertFalse(ShadowAlertDialog.getLatestAlertDialog().isShowing());
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(5));
    }

    private void fillAddNewItemDialog() throws Exception {
        activity.fab.performClick();
        final String testName = "test name";
        ShadowAlertDialog shadowAlertDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        assertThat(shadowAlertDialog, notNullValue());
        EditText ediText = (EditText) shadowAlertDialog.getView().findViewById(R.id.tags_dialog_name);
        ediText.setText(testName);
        assertThat(activity.getString(R.string.tags_new_tag_dialog), equalTo(shadowAlertDialog.getTitle()));
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
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));
        activity.recyclerView.getChildAt(0).findViewById(R.id.tag_button).performLongClick();

        ShadowAlertDialog shadowAlertDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        assertThat(shadowAlertDialog, notNullValue());
        ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
    }

    @Test
    public void testSelectTag() throws Exception {
        activity.recyclerView.getChildAt(0).findViewById(R.id.tag_button).performClick();
        assertTrue(shadowOf(activity).isFinishing());
        Intent resultIntent = shadowOf(activity).getResultIntent();
        assertThat(resultIntent, CoreMatchers.notNullValue());
        assertThat(resultIntent.getLongExtra(TagsActivity.EXTRA_TAG_ID, -1L), not(equalTo(-1L)));
    }

    @Test
    public void testSearch() throws Exception {
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));

        activity.searchView.performClick();
        activity.searchView.setQuery("Tag", true);

        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
    }
}