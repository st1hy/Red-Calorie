package com.github.st1hy.countthemcalories.activities.tags.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.widget.EditText;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.functions.Action1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TagsActivityTest {

    private TagsActivity activity;
    private TagsPresenter presenterMock;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(TagsActivity.class);
        presenterMock = Mockito.mock(TagsPresenter.class);
        activity.presenter = presenterMock;
    }

    @Test
    public void testActivityStart() {
        assertThat(activity, notNullValue());
        assertThat(activity.presenter, notNullValue());
        assertThat(activity.toolbar, notNullValue());
        assertThat(activity.getComponent(), notNullValue());
        assertThat(activity.component, notNullValue());
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void testShowEditTextDialog() throws Exception {
        final String testName = "test name";
        final AtomicBoolean isCalled = new AtomicBoolean(false);
        activity.showEditTextDialog(R.string.tags_new_tag_dialog)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        isCalled.set(true);
                        assertEquals(testName, s);
                    }
                });
        ShadowAlertDialog shadowAlertDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        assertThat(shadowAlertDialog, notNullValue());
        EditText ediText = (EditText) shadowAlertDialog.getView().findViewById(R.id.tags_dialog_name);
        ediText.setText(testName);
        assertThat(activity.getString(R.string.tags_new_tag_dialog), equalTo(shadowAlertDialog.getTitle()));
        ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        assertTrue(isCalled.get());
    }

    @Test
    public void testStart() throws Exception {
        activity.onStart();
        verify(presenterMock).onStart();
    }

    @Test
    public void testOnStop() throws Exception {
        activity.onStop();
        verify(presenterMock).onStop();
    }

    @Test
    public void testLongPressItemToRemove() throws Exception {


    }
}