package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.ImageSource;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowDialog;

import static android.app.Activity.RESULT_OK;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity.REQUEST_PICK_IMAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AddMealActivityRoboTest {
    private AddMealActivity activity;
    private AddMealPresenter presenterMock;
    private Picasso mockedPicasso;
    private RequestCreator mockedRequestCreator;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(AddMealActivity.class);
        presenterMock = Mockito.mock(AddMealPresenter.class);
        activity.presenter = presenterMock;

        mockedPicasso = Mockito.mock(Picasso.class);
        mockedRequestCreator = Mockito.mock(RequestCreator.class);
        when(mockedPicasso.load(any(Uri.class))).thenReturn(mockedRequestCreator);
        when(mockedRequestCreator.centerCrop()).thenReturn(mockedRequestCreator);
        when(mockedRequestCreator.fit()).thenReturn(mockedRequestCreator);
        activity.picasso = mockedPicasso;
    }


    @Test
    public void testActivityStart() {
        assertThat(activity, notNullValue());
        assertThat(activity.presenter, notNullValue());
        assertThat(activity.picasso, notNullValue());
        assertThat(activity.toolbar, notNullValue());
        assertThat(activity.getComponent(), notNullValue());
        assertThat(activity.component, notNullValue());
        assertThat(activity.picassoLoaderCallback, notNullValue());
        assertThat(activity.subscriptions, notNullValue());
    }

    @Test
    public void testSaveButtonAction() throws Exception {
        activity.saveButton.performClick();

        verify(presenterMock, only()).onSaveButtonClicked();
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

    @Test
    public void testShowSelectImageDialog() throws Exception {
        activity.showSelectImageInputDialog();
        ShadowDialog shadowDialog = shadowOf(RuntimeEnvironment.application).getLatestDialog();
        assertThat(shadowDialog, notNullValue());
    }

    @Ignore("robolectric does not see it as alert dialog, clicking on text fails to inflate layout")
    @Test
    public void testClickingOnDialogItem() throws Exception {
        activity.showSelectImageInputDialog();
        ShadowAlertDialog shadowAlertDialog = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();
        shadowAlertDialog.clickOnItem(0);
//        ShadowDialog shadowDialog = shadowOf(RuntimeEnvironment.application).getLatestDialog();
//        shadowDialog.clickOnText(R.string.add_meal_image_select_from_gallery);
        verify(presenterMock, only()).onSelectedImageSource(ImageSource.GALLERY);
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
        }).when(mockedRequestCreator).into(eq(activity.mealImage), any(Callback.class));
        activity.setImageToView(mockedUri);
        verify(mockedPicasso).load(mockedUri);
        verify(presenterMock).onImageLoadingSuccess();
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
        }).when(mockedRequestCreator).into(eq(activity.mealImage), any(Callback.class));
        activity.setImageToView(mockedUri);
        verify(mockedPicasso).load(mockedUri);
        verify(presenterMock).onImageLoadingFailed();
    }

    @Test(expected = IllegalStateException.class)
    public void testSetImagePicassoThrowsException() throws Exception {
        Uri mockedUri = Mockito.mock(Uri.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new IllegalStateException("Picasso failed");
            }
        }).when(mockedRequestCreator).into(eq(activity.mealImage), any(Callback.class));
        activity.setImageToView(mockedUri);
    }

    @Test
    public void testDestroy() throws Exception {
        activity.onDestroy();

        verify(mockedPicasso).cancelRequest(activity.mealImage);
    }
}
