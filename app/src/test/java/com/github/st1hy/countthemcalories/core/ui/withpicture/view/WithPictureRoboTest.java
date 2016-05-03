package com.github.st1hy.countthemcalories.core.ui.withpicture.view;


import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenterImp;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.ui.withpicture.presenter.WithPicturePresenterImp;
import com.github.st1hy.countthemcalories.testrunner.RxRobolectricGradleTestRunner;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.functions.Action1;
import rx.plugins.TestRxPlugins;

import static android.app.Activity.RESULT_OK;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static com.github.st1hy.countthemcalories.core.ui.withpicture.view.WithPictureActivity.REQUEST_PICK_IMAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RxRobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class WithPictureRoboTest {

    private WithPictureActivity activity;
    private WithPicturePresenterImp presenterMock;
    private Picasso mockedPicasso;
    private ImageView mockedImageView;
    private RequestCreator mockedRequestCreator;


    @Before
    public void setup() {
        TestRxPlugins.registerImmediateHook();
        WithPictureActivityTest activity = Robolectric.setupActivity(WithPictureActivityTest.class);
        presenterMock = Mockito.mock(AddMealPresenterImp.class);
        activity.presenter = presenterMock;
        mockedImageView = Mockito.mock(ImageView.class);
        activity.mockedImageView = mockedImageView;
        mockedPicasso = Mockito.mock(Picasso.class);
        activity.picasso = mockedPicasso;
        this.activity = activity;

        mockedRequestCreator = Mockito.mock(RequestCreator.class);
        when(mockedPicasso.load(any(Uri.class))).thenReturn(mockedRequestCreator);
        when(mockedRequestCreator.centerCrop()).thenReturn(mockedRequestCreator);
        when(mockedRequestCreator.fit()).thenReturn(mockedRequestCreator);
    }

    private static class WithPictureActivityTest extends WithPictureActivity {
        ImageView mockedImageView;

        @Override
        protected ImageView getImageView() {
            return mockedImageView;
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
        verify(presenterMock, only()).onImageReceived(mockedUri);
    }

    @Test
    public void testActivityResultOther() throws Exception {
        Intent intent = Mockito.mock(Intent.class);
        activity.onActivityResult(-1, -1, intent);
        verifyZeroInteractions(presenterMock);
    }

    @Test
    public void testSetImageToViewSuccess() throws Exception {
        Uri mockedUri = Mockito.mock(Uri.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = (Callback) invocation.getArguments()[1];
                callback.onSuccess();
                return null;
            }
        }).when(mockedRequestCreator).into(eq(mockedImageView), any(Callback.class));
        final AtomicBoolean isCalled = new AtomicBoolean(false);
        activity.showImage(mockedUri)
                .subscribe(new Action1<RxPicasso.PicassoEvent>() {
                    @Override
                    public void call(RxPicasso.PicassoEvent event) {
                        isCalled.set(true);
                        assertThat(RxPicasso.PicassoEvent.SUCCESS, equalTo(event));
                    }
                });
        assertTrue(isCalled.get());
        verify(mockedPicasso).load(mockedUri);
    }

    @Test
    public void testSetImageToViewFailure() throws Exception {
        Uri mockedUri = Mockito.mock(Uri.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = (Callback) invocation.getArguments()[1];
                callback.onError();
                return null;
            }
        }).when(mockedRequestCreator).into(eq(mockedImageView), any(Callback.class));
        final AtomicBoolean isCalled = new AtomicBoolean(false);
        activity.showImage(mockedUri)
                .subscribe(new Action1<RxPicasso.PicassoEvent>() {
                    @Override
                    public void call(RxPicasso.PicassoEvent event) {
                        isCalled.set(true);
                        assertThat(RxPicasso.PicassoEvent.ERROR, equalTo(event));
                    }
                });
        assertTrue(isCalled.get());
        verify(mockedPicasso).load(mockedUri);
    }


    @Test
    public void tesOnStop() throws Exception {
        activity.onStop();
        verify(presenterMock).onStop();
    }
}
