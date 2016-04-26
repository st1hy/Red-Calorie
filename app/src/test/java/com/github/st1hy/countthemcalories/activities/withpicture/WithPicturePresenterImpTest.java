package com.github.st1hy.countthemcalories.activities.withpicture;

import android.net.Uri;

import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
    private WithPicturePresenter presenter;


    @Before
    public void setup() {
        presenter = new WithPicturePresenterImp(view, permissionsHelper);
        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));
    }

    @Test
    public void testAddingImageFromCamera() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                presenter.onSelectedImageSource(ImageSource.CAMERA);
                return null;
            }
        }).when(view).showSelectImageInputDialog();

        presenter.onImageClicked();

        verify(permissionsHelper, times(1)).checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class));
        verify(view, times(1)).showSelectImageInputDialog();
        verify(view, times(1)).openCameraAndGetPicture();
        verifyNoMoreInteractions(view, permissionsHelper);
    }

    @Test
    public void testAddingImageFromGallery() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                presenter.onSelectedImageSource(ImageSource.GALLERY);
                return null;
            }
        }).when(view).showSelectImageInputDialog();

        presenter.onImageClicked();

        verify(permissionsHelper, times(1)).checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class));
        verify(view, times(1)).showSelectImageInputDialog();
        verify(view, times(1)).pickImageFromGallery();
        verifyNoMoreInteractions(view, permissionsHelper);
    }

    @Test
    public void testDisplayImage() {
        Uri data = Mockito.mock(Uri.class);
        presenter.onImageReceived(data);

        verify(view, only()).setImageToView(data);
    }
}