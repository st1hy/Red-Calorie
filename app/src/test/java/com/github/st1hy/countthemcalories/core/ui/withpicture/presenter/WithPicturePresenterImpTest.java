package com.github.st1hy.countthemcalories.core.ui.withpicture.presenter;

import android.net.Uri;

import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.core.ui.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.core.ui.withpicture.view.WithPictureView;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
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
    @Mock
    private WithPictureModel model;
    private WithPicturePresenter presenter;


    @Before
    public void setup() {
        presenter = new WithPicturePresenterImp(view, permissionsHelper, model);
        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));
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
        presenter.onImageReceived(data);

        verify(view, only()).setImageToView(data);
    }
}