package com.github.st1hy.countthemcalories.core.withpicture.presenter;

import android.net.Uri;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.core.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.plugins.TestRxPlugins;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class WithPicturePresenterImpTest {

    @Mock
    private WithPictureView view;
    @Mock
    private PermissionsHelper permissionsHelper;
    @Mock
    private WithPictureModel model;
    @Mock
    private ImageView imageView;
    @Mock
    private Picasso picasso;
    @Mock
    private RequestCreator requestCreator;
    private WithPicturePresenterImp presenter;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TestRxPlugins.registerImmediateMainThreadHook();
        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));
        when(picasso.load(any(Uri.class))).thenReturn(requestCreator);
        when(requestCreator.centerCrop()).thenReturn(requestCreator);
        when(requestCreator.fit()).thenReturn(requestCreator);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = (Callback) invocation.getArguments()[1];
                callback.onSuccess();
                return null;
            }
        }).when(requestCreator).into(any(ImageView.class), any(Callback.class));
        when(view.getImageView()).thenReturn(imageView);
        when(view.getSelectPictureObservable())
                .thenReturn(Observable.<Void>empty());
        when(view.getPictureSelectedObservable())
                .thenReturn(Observable.<Uri>empty());

        presenter = new WithPicturePresenterImp(view, permissionsHelper, model,picasso);
    }

    @After
    public void tearDown() throws Exception {
        presenter.onStop();
        TestRxPlugins.reset();
    }

    @Test
    public void testOnStart() {
        presenter.onStart();

        verify(view).getSelectPictureObservable();
        verify(view).getPictureSelectedObservable();

        verifyNoMoreInteractions(view, permissionsHelper, model, picasso, imageView, requestCreator,
                imageView);
    }

    @Test
    public void testAddingImageFromCamera() {
        when(view.getSelectPictureObservable())
                .thenReturn(Observable.<Void>just(null));
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(1));

        presenter.onStart();

        verify(view).getSelectPictureObservable();
        verify(view).getPictureSelectedObservable();
        verify(permissionsHelper).checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class));
        verify(model).getImageSourceDialogTitleResId();
        verify(model).getSelectImageSourceOptions();
        verify(model).hasImage();
        verify(view).showAlertDialog(anyInt(), anyInt());
        verify(view).openCameraAndGetPicture();

        verifyNoMoreInteractions(view, permissionsHelper, model, picasso, imageView, requestCreator,
                imageView);
    }

    @Test
    public void testAddingImageFromGallery() {
        when(view.getSelectPictureObservable())
                .thenReturn(Observable.<Void>just(null));
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(0));

        presenter.onStart();

        verify(view).getSelectPictureObservable();
        verify(view).getPictureSelectedObservable();
        verify(permissionsHelper).checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class));
        verify(model).getImageSourceDialogTitleResId();
        verify(model).getSelectImageSourceOptions();
        verify(model).hasImage();
        verify(view).showAlertDialog(anyInt(), anyInt());
        verify(view).pickImageFromGallery();
        verifyNoMoreInteractions(view, permissionsHelper, model, picasso, imageView, requestCreator,
                imageView);
    }

    @Test
    public void testOnStop() throws Exception {
        presenter.onStop();

        assertThat(presenter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(presenter.subscriptions.hasSubscriptions(), equalTo(false));

        verifyNoMoreInteractions(view, permissionsHelper, model, picasso, imageView, requestCreator,
                imageView);
    }

    @Test
    public void testSetImageToViewSuccess() throws Exception {
        Uri mockedUri = Mockito.mock(Uri.class);
        when(view.getPictureSelectedObservable())
                .thenReturn(Observable.just(mockedUri));

        presenter.onStart();

        verify(view).getSelectPictureObservable();
        verify(view).getPictureSelectedObservable();
        verify(picasso).load(mockedUri);
        verify(view).getImageView();
        verify(view).showImageOverlay();
        verify(picasso).cancelRequest(any(ImageView.class));
        verify(requestCreator).centerCrop();
        verify(requestCreator).fit();
        verify(requestCreator).into(any(ImageView.class), any(Callback.class));

        verifyNoMoreInteractions(view, permissionsHelper, model, picasso, imageView, requestCreator,
                imageView);
    }

    @Test
    public void testRemovePicture() throws Exception {
        when(view.getSelectPictureObservable())
                .thenReturn(Observable.<Void>just(null));
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(2));
        when(model.hasImage()).thenReturn(true);

        presenter.onStart();

        verify(view).getSelectPictureObservable();
        verify(view).getPictureSelectedObservable();
        verify(permissionsHelper).checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class));
        verify(model).getImageSourceDialogTitleResId();
        verify(model).getSelectImageSourceAndRemoveOptions();
        verify(model).hasImage();
        verify(view).showAlertDialog(anyInt(), anyInt());
        verify(view).hideImageOverlay();
        verify(view).getImageView();
        verify(model).getSelectImageDrawableRes();
        verify(imageView).setImageResource(anyInt());

        verifyNoMoreInteractions(view, permissionsHelper, model, picasso, imageView, requestCreator,
                imageView);


    }
}