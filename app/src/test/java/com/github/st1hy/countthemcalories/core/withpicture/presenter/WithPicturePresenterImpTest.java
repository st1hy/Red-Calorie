package com.github.st1hy.countthemcalories.core.withpicture.presenter;

import android.net.Uri;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.functions.Action1;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RxMockitoJUnitRunner.class)
public class WithPicturePresenterImpTest {

    @Mock
    private WithPictureView view;
    @Mock
    private PermissionsHelper permissionsHelper;
    @Mock
    private WithPictureModel model;
    @Mock
    private Picasso picasso;
    @Mock
    private RequestCreator requestCreator;
    private WithPicturePresenterImp presenter;


    @Before
    public void setup() {
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

        presenter = new WithPicturePresenterImp(view, permissionsHelper, model,picasso);
    }

    @Test
    public void testOnStart() {

    }

    @Test
    public void testAddingImageFromCamera() {
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(1));

        presenter.onImageClicked();

        verify(permissionsHelper, times(1)).checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class));
        verify(view, times(1)).showAlertDialog(anyInt(), anyInt());
        verify(view, times(1)).openCameraAndGetPicture();
        verifyNoMoreInteractions(view, permissionsHelper);
    }

    @Test
    public void testAddingImageFromGallery() {
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(0));

        presenter.onImageClicked();

        verify(permissionsHelper, times(1)).checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class));
        verify(view, times(1)).showAlertDialog(anyInt(), anyInt());
        verify(view, times(1)).pickImageFromGallery();
        verifyNoMoreInteractions(view, permissionsHelper);
    }

    @Test
    public void testDisplayImage() {
        Uri data = Mockito.mock(Uri.class);
        when(view.showImage(data)).thenReturn(Observable.just(RxPicasso.PicassoEvent.ERROR));

        presenter.onImageReceived(data);
        verify(view, only()).showImage(data);
    }

    @Test
    public void testOnStop() throws Exception {
        presenter.onStop();

        assertThat(presenter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(presenter.subscriptions.hasSubscriptions(), equalTo(false));
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
                        MatcherAssert.assertThat(RxPicasso.PicassoEvent.SUCCESS, equalTo(event));
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
                        MatcherAssert.assertThat(RxPicasso.PicassoEvent.ERROR, equalTo(event));
                    }
                });
        assertTrue(isCalled.get());
        verify(mockedPicasso).load(mockedUri);
    }
}