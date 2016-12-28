package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.LoadedSource;
import com.github.st1hy.countthemcalories.testutils.OptionalMatchers;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.google.common.base.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.plugins.TestRxPlugins;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class AddMealPresenterTest {

    @Mock
    private AddMealView view;
    @Mock
    private PermissionsHelper permissionsHelper;
    @Mock
    private AddMealModel model;
    @Mock
    private ImageHolderDelegate imageHolderDelegate;
    private AddMealPresenterImp presenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TestRxPlugins.registerImmediateMainThreadHook();
        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));
        when(model.getLoading()).thenReturn(Observable.<Void>just(null));
        when(model.getName()).thenReturn("Name");
        when(model.getImageUri()).thenReturn(Uri.EMPTY);
        when(model.getNameError()).thenReturn(Optional.<String>absent());
        when(model.getIngredientsError()).thenReturn(Optional.<String>absent());
        when(view.getPictureSelectedObservable()).thenReturn(Observable.<Uri>empty());
        when(view.getSelectPictureObservable()).thenReturn(Observable.<Void>empty());
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>empty());
        when(view.getAddIngredientObservable()).thenReturn(Observable.<Void>empty());
        when(view.getSaveClickedObservable()).thenReturn(Observable.<Void>empty());
        when(imageHolderDelegate.getLoadingObservable()).thenReturn(Observable.<LoadedSource>empty());

        presenter = new AddMealPresenterImp(view, permissionsHelper, model, imageHolderDelegate);
    }

    @After
    public void tearDown() throws Exception {
        TestRxPlugins.reset();
    }

    @Test
    public void testOnStartNotLoaded() throws Exception {
        when(model.getLoading()).thenReturn(Observable.<Void>empty());

        presenter.onStart();

        verify(model).getLoading();

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testOnStart() {
        presenter.onStart();

        testVerifyOnStart();

        testVerifyNoMoreInteraction();
    }

    private void testVerifyOnStart() {
        testVerifyOnStartNoImage();
        verify(imageHolderDelegate).setImageUri(argThat(OptionalMatchers.<Uri>isAbsent()));
    }

    private void testVerifyOnStartNoImage() {
        verify(model).getLoading();
        verify(model).getImageUri();
        verify(model).getName();
        verify(view).setName("Name");
        verify(view).getNameObservable();
        verify(view).getAddIngredientObservable();
        verify(view).getSaveClickedObservable();
        verify(view).getSelectPictureObservable();
        verify(view).getPictureSelectedObservable();
        verify(imageHolderDelegate).onAttached();
        verify(imageHolderDelegate).getLoadingObservable();
    }

    @Test
    public void testSetupViewWithImage() throws Exception {
        final Uri uri = mock(Uri.class);

        when(model.getImageUri()).thenReturn(uri);
        when(imageHolderDelegate.getLoadingObservable())
                .thenReturn(Observable.just(LoadedSource.PICASSO));

        presenter.onStart();

        testVerifyOnStartNoImage();
        verify(imageHolderDelegate).setImageUri(argThat(OptionalMatchers.equalTo(uri)));
        verify(view).showImageOverlay();

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testNameChanged() throws Exception {
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just("Name", "Name2"));

        presenter.onStart();

        testVerifyOnStart();

        verify(model).setName("Name2");
        verify(model).getNameError();
        verify(view).showNameError(argThat(OptionalMatchers.<String>isAbsent()));

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testAddIngredientClicked() throws Exception {
        when(view.getAddIngredientObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();

        testVerifyOnStart();
        verify(view).openAddIngredient();

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testSave() {
        when(model.saveToDatabase()).thenReturn(Observable.<Void>just(null));
        when(view.getSaveClickedObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();

        testVerifyOnStart();
        verify(model).getNameError();
        verify(model).getIngredientsError();
        verify(model).saveToDatabase();
        verify(view).onMealSaved();
        verify(view).showNameError(argThat(OptionalMatchers.<String>isAbsent()));
        verify(view).showSnackbarError(argThat(OptionalMatchers.<String>isAbsent()));
        testVerifyNoMoreInteraction();
    }

    @Test
    public void testSaveErrors() {
        when(model.getNameError()).thenReturn(Optional.of("error"));
        when(model.getIngredientsError()).thenReturn(Optional.of("error2"));
        when(view.getSaveClickedObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();

        testVerifyOnStart();
        verify(model).getNameError();
        verify(model).getIngredientsError();
        verify(view).showNameError(argThat(OptionalMatchers.equalTo("error")));
        verify(view).showSnackbarError(argThat(OptionalMatchers.equalTo("error2")));
        testVerifyNoMoreInteraction();
    }

    @Test
    public void testOnSaveInstance() throws Exception {
        Bundle bundle = new Bundle();
        presenter.onSaveState(bundle);

        verify(model).onSaveState(bundle);
        testVerifyNoMoreInteraction();
    }

    private void testVerifyNoMoreInteraction() {
        verifyNoMoreInteractions(view, permissionsHelper, model, imageHolderDelegate);
    }
}
