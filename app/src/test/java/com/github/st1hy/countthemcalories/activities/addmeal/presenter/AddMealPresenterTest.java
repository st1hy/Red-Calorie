package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.net.Uri;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class AddMealPresenterTest {

    @Mock
    private AddMealView view;
    private AddMealPresenter presenter;

    @Before
    public void setup() {
        presenter = new AddMealPresenterImp(view);
    }

    @Test
    public void testSave() {
        presenter.onSaveButtonClicked();
        verify(view, only()).openOverviewActivity();
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

        verify(view, times(1)).showSelectImageInputDialog();
        verify(view, times(1)).openCameraAndGetPicture();
        verifyNoMoreInteractions(view);
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

        verify(view, times(1)).showSelectImageInputDialog();
        verify(view, times(1)).pickImageFromGallery();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testDisplayImage() {
        Uri data = Mockito.mock(Uri.class);
        presenter.onImageReceived(data);

        verify(view, only()).setImageToView(data);
    }
}
