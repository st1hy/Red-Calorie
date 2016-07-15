package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.testutils.OptionalMatchers;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.google.common.base.Optional;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.plugins.TestRxPlugins;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class AddMealPresenterTest {

    @Mock
    private AddMealView view;
    @Mock
    private PermissionsHelper permissionsHelper;
    @Mock
    private AddMealModel model;
    @Mock
    private Picasso picasso;
    @Mock
    private RequestCreator requestCreator;
    private AddMealPresenterImp presenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TestRxPlugins.registerImmediateMainThreadHook();
        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));
        when(picasso.load(any(Uri.class))).thenReturn(requestCreator);
        when(requestCreator.centerCrop()).thenReturn(requestCreator);
        when(requestCreator.fit()).thenReturn(requestCreator);
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
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = (Callback) invocation.getArguments()[1];
                callback.onSuccess();
                return null;
            }
        }).when(requestCreator).into(any(ImageView.class), any(Callback.class));

        presenter = new AddMealPresenterImp(view, permissionsHelper, model, picasso);
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

        verifyNoMoreInteractions(view, permissionsHelper, model, picasso);
    }

    @Test
    public void testOnStart() {
        presenter.onStart();

        testVerifyOnStart();

        verifyNoMoreInteractions(view, permissionsHelper, model, picasso);
    }

    private void testVerifyOnStart() {
        verify(model).getLoading();
        verify(model).getImageUri();
        verify(model).getName();
        verify(view).setName("Name");
        verify(view).getNameObservable();
        verify(view).getAddIngredientObservable();
        verify(view).getSaveClickedObservable();
        verify(view).getSelectPictureObservable();
        verify(view).getPictureSelectedObservable();
    }

    @Test
    public void testSetupViewWithImage() throws Exception {
        final Uri uri = mock(Uri.class);

        when(model.getImageUri()).thenReturn(uri);

        presenter.onStart();

        testVerifyOnStart();

        verify(picasso).load(uri);
        verify(view).showImageOverlay();
        verify(view).getImageView();
        verify(picasso).cancelRequest(any(ImageView.class));

        verifyNoMoreInteractions(view, permissionsHelper, model, picasso);
    }

    @Test
    public void testNameChanged() throws Exception {
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just("Name", "Name2"));

        presenter.onStart();

        testVerifyOnStart();

        verify(model).setName("Name2");
        verify(model).getNameError();
        verify(view).showNameError(argThat(OptionalMatchers.<String>isAbsent()));

        verifyNoMoreInteractions(view, permissionsHelper, model, picasso);
    }

    @Test
    public void testAddIngredientClicked() throws Exception {
        when(view.getAddIngredientObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();

        testVerifyOnStart();
        verify(view).openAddIngredient();

        verifyNoMoreInteractions(view, permissionsHelper, model, picasso);
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
        verifyNoMoreInteractions(view, permissionsHelper, model, picasso);
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
        verifyNoMoreInteractions(view, permissionsHelper, model, picasso);
    }

    @Test
    public void testOnSaveInstance() throws Exception {
        Bundle bundle = new Bundle();
        presenter.onSaveState(bundle);

        verify(model).onSaveState(bundle);
        verifyNoMoreInteractions(view, permissionsHelper, model, picasso);
    }
}
