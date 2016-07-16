package com.github.st1hy.countthemcalories.core.withpicture.view;


import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.hamcrest.Matcher;
import org.junit.Assert;
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

import rx.Observable;
import rx.functions.Action1;

import static android.app.Activity.RESULT_OK;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureActivity.REQUEST_PICK_IMAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class WithPictureRoboTest {

    private WithPictureActivity activity;

    @Before
    public void setup() {
        WithPictureActivityTest activity = Robolectric.setupActivity(WithPictureActivityTest.class);
        activity.mockedImageView = Mockito.mock(ImageView.class);
        this.activity = activity;
    }

    private static class WithPictureActivityTest extends WithPictureActivity {
        ImageView mockedImageView;

        @NonNull
        @Override
        public ImageView getImageView() {
            return mockedImageView;
        }

        @NonNull
        @Override
        public Observable<Void> getSelectPictureObservable() {
            return Observable.empty();
        }

        @Override
        public void showImageOverlay() {
        }

        @Override
        public void hideImageOverlay() {
        }
    }

    @Test
    public void testClickingOnDialogItem() throws Exception {
        final AtomicBoolean isCalled = new AtomicBoolean(false);
        activity.showAlertDialog(R.string.add_ingredient_image_select_title, R.array.add_ingredient_image_select_options)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        isCalled.set(true);
                        assertThat(integer, equalTo(1));
                    }
                });
        ShadowAlertDialog shadowAlertDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        assertThat(shadowAlertDialog, notNullValue());
        shadowAlertDialog.clickOnItem(1);
        assertTrue(isCalled.get());
    }

    @Test
    public void testOpenCamera() throws Exception {
        activity.openCameraAndGetPicture();
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, hasAction(MediaStore.ACTION_IMAGE_CAPTURE));
    }

    private final Matcher<Intent> galleryIntentMatcher = allOf(
            hasAction(Intent.ACTION_CHOOSER),
            hasExtras(
                    allOf(
                            hasEntry(Intent.EXTRA_INTENT,
                                    allOf(
                                            hasAction(Intent.ACTION_GET_CONTENT),
                                            hasType("image/*")
                                    )
                            ),
                            hasEntry(Intent.EXTRA_INITIAL_INTENTS,
                                    hasItemInArray(
                                            allOf(
                                                    hasAction(Intent.ACTION_PICK),
                                                    hasType("image/*")
                                            )
                                    )
                            )
                    )
            )
    );

    @Test
    public void testPickImageFromGallery() throws Exception {
        activity.pickImageFromGallery();
        Intent resultIntent = shadowOf(activity).peekNextStartedActivity();
        assertThat(resultIntent, galleryIntentMatcher);
    }

    @Test
    public void testActivityResultImageReceived() throws Exception {
        final Intent testIntent = new Intent();
        final Uri mockedUri = Mockito.mock(Uri.class);
        testIntent.setData(mockedUri);
        activity.onActivityResult(REQUEST_PICK_IMAGE, RESULT_OK, testIntent);
        Uri first = activity.getPictureSelectedObservable().toBlocking().first();
        Assert.assertThat(first, equalTo(mockedUri));
    }

    @Test
    public void testActivityResultOther() throws Exception {
        Intent intent = Mockito.mock(Intent.class);
        final AtomicBoolean isCalled = new AtomicBoolean(false);
        activity.getPictureSelectedObservable().subscribe(new Action1<Uri>() {
            @Override
            public void call(Uri uri) {
                isCalled.set(true);
            }
        });
        activity.onActivityResult(-1, -1, intent);
        assertThat(isCalled.get(), equalTo(false));
    }

}
