package com.github.st1hy.countthemcalories.activities.tags.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.shadows.ShadowSnackbar;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.github.st1hy.countthemcalories.testutils.TimberUtils;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName,
        shadows = {ShadowSnackbar.class})
@LargeTest
public class TagsActivityRoboTest {
    public static final Tag[] exampleTags = new Tag[]{
            new Tag(1L, "Tag 1"),
            new Tag(2L, "Tag 2"),
            new Tag(3L, "Meal")
    };

    private TagsActivity activity;

    @Before
    public void setup() throws Exception {
        Timber.plant(TimberUtils.ABOVE_WARN);
        TagsDaoAdapter.debounceTime = 0;
        TestRxPlugins.registerImmediateHookIO();
        addExampleTags(OverviewActivityRoboTest.prepareDatabase());
        Intent intent = new Intent(TagsActivity.ACTION_PICK_TAG);
        activity = Robolectric.buildActivity(TagsActivity.class)
                .withIntent(intent)
                .setup()
                .get();
    }

    public static void addExampleTags(@NonNull DaoSession session) {
        TagDao tagDao = session.getTagDao();
        tagDao.deleteAll();
        tagDao.insertInTx(exampleTags);
        tagDao.loadAll();
        assertEquals(3, tagDao.loadAll().size());
    }

    @After
    public void tearDown() throws Exception {
        Timber.uprootAll();
        TestRxPlugins.reset();
        TagsDaoAdapter.debounceTime = 250;
    }

    @Test
    public void testActivityStart() {
        assertThat(activity, notNullValue());
        assertThat(activity.presenter, notNullValue());
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
    public void testRemoveItem() throws Exception {
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));
        activity.recyclerView.getChildAt(0).findViewById(R.id.tags_item_delete).performClick();

        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
    }

    @Test
    public void testSelectTag() throws Exception {
        activity.recyclerView.getChildAt(0).findViewById(R.id.tag_item_button).performClick();
        assertTrue(shadowOf(activity).isFinishing());
        Intent resultIntent = shadowOf(activity).getResultIntent();
        assertThat(resultIntent, CoreMatchers.notNullValue());
        assertThat(resultIntent.getLongExtra(TagsActivity.EXTRA_TAG_ID, -1L), not(equalTo(-1L)));
    }

    @Test
    public void testSearch() throws Exception {
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));

        activity.searchView.performClick();
        activity.searchView.setQuery("Tag");

        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
    }

    @Test
    public void testUndoRemove() throws Exception {
        testRemoveItem();
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(3));
        ShadowSnackbar.getLatest().performAction();
        assertThat(activity.recyclerView.getAdapter().getItemCount(), equalTo(4));
    }

    @Test
    public void testEditTag() throws Exception {
        TextView name = (TextView) activity.recyclerView.getChildAt(0).findViewById(R.id.tags_item_name);
        assertThat(name.getText().toString(), equalTo(exampleTags[2].getName()));
        activity.recyclerView.getChildAt(0).findViewById(R.id.tags_item_edit).performClick();

        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(latestAlertDialog);
        EditText edit = (EditText) shadowOf(latestAlertDialog).getView().findViewById(R.id.tags_dialog_name);
        assertNotNull(edit);
        edit.setText("A new name");
        latestAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        name = (TextView) activity.recyclerView.getChildAt(0).findViewById(R.id.tags_item_name);
        assertThat(name.getText().toString(), equalTo("A new name"));
    }
}