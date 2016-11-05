package com.github.st1hy.countthemcalories.core.picture.presenter;

import android.net.Uri;

import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.core.picture.PicturePresenterImp;
import com.github.st1hy.countthemcalories.core.picture.PictureViewController;
import com.github.st1hy.countthemcalories.core.picture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.picture.imageholder.LoadedSource;
import com.github.st1hy.countthemcalories.core.picture.PictureModel;
import com.github.st1hy.countthemcalories.testutils.OptionalMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.plugins.TestRxPlugins;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WithPicturePresenterImpTest {

    @Mock
    private PictureViewController view;
    @Mock
    private PermissionsHelper permissionsHelper;
    @Mock
    private PictureModel model;
    @Mock
    private ImageHolderDelegate imageHolderDelegate;
    private PicturePresenterImp presenter;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TestRxPlugins.registerImmediateMainThreadHook();
        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));
        when(view.getSelectPictureObservable())
                .thenReturn(Observable.<Void>empty());
        when(view.getPictureSelectedObservable())
                .thenReturn(Observable.<Uri>empty());
        when(imageHolderDelegate.getLoadingObservable())
                .thenReturn(Observable.<LoadedSource>empty());

        presenter = new PicturePresenterImp(view, permissionsHelper, dialogView, pictureController, model,imageHolderDelegate);
    }

    @After
    public void tearDown() throws Exception {
        presenter.onStop();
        TestRxPlugins.reset();
    }

    @Test
    public void testOnStart() {
        presenter.onStart();
        testVerifyStart();
        testVerifyNoMoreInteraction();
    }

    private void testVerifyStart() {
        verify(view).getSelectPictureObservable();
        verify(view).getPictureSelectedObservable();
        verify(imageHolderDelegate).onAttached();
        verify(imageHolderDelegate).getLoadingObservable();
    }

    @Test
    public void testAddingImageFromCamera() {
        when(view.getSelectPictureObservable())
                .thenReturn(Observable.<Void>just(null));
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(1));

        presenter.onStart();

        testVerifyStart();
        verify(permissionsHelper).checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class));
        verify(model).getImageSourceDialogTitleResId();
        verify(model).getSelectImageSourceOptions();
        verify(model).hasImage();
        verify(view).showAlertDialog(anyInt(), anyInt());
        verify(view).openCameraAndGetPicture();

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testAddingImageFromGallery() {
        when(view.getSelectPictureObservable())
                .thenReturn(Observable.<Void>just(null));
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(0));

        presenter.onStart();

        testVerifyStart();
        verify(permissionsHelper).checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class));
        verify(model).getImageSourceDialogTitleResId();
        verify(model).getSelectImageSourceOptions();
        verify(model).hasImage();
        verify(view).showAlertDialog(anyInt(), anyInt());
        verify(view).pickImageFromGallery();
        testVerifyNoMoreInteraction();
    }

    @Test
    public void testOnStop() throws Exception {
        presenter.onStop();

        assertThat(presenter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(presenter.subscriptions.hasSubscriptions(), equalTo(false));
        verify(imageHolderDelegate).onDetached();

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testSetImageToViewSuccess() throws Exception {
        Uri mockedUri = Mockito.mock(Uri.class);
        when(view.getPictureSelectedObservable())
                .thenReturn(Observable.just(mockedUri));
        when(imageHolderDelegate.getLoadingObservable())
                .thenReturn(Observable.just(LoadedSource.PICASSO));

        presenter.onStart();

        testVerifyStart();
        verify(imageHolderDelegate).displayImage(argThat(OptionalMatchers.equalTo(mockedUri)));
        verify(view).showImageOverlay();

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testRemovePicture() throws Exception {
        when(view.getSelectPictureObservable())
                .thenReturn(Observable.<Void>just(null));
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(2));
        when(model.hasImage()).thenReturn(true);
        when(imageHolderDelegate.getLoadingObservable())
                .thenReturn(Observable.just(LoadedSource.PLACEHOLDER));

        presenter.onStart();

        testVerifyStart();
        verify(permissionsHelper).checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class));
        verify(model).getImageSourceDialogTitleResId();
        verify(model).getSelectImageSourceAndRemoveOptions();
        verify(model).hasImage();
        verify(view).showAlertDialog(anyInt(), anyInt());
        verify(view).hideImageOverlay();

        testVerifyNoMoreInteraction();


    }

    private void testVerifyNoMoreInteraction() {
        verifyNoMoreInteractions(view, permissionsHelper, model, imageHolderDelegate);
    }
}