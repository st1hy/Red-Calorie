package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientView;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;
import com.github.st1hy.countthemcalories.testutils.TestError;
import com.google.common.base.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import rx.Observable;

import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.NO_NAME;
import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.NO_VALUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RxMockitoJUnitRunner.class)
public class AddIngredientPresenterImpTest {
    final String testUnit = "test unit";
    final String testName = "test name";
    final String testEnergy = "test energy";

    @Mock
    private AddIngredientView view;
    @Mock
    private PermissionsHelper permissionsHelper;
    @Mock
    private AddIngredientModel model;
    @Mock
    private Uri testUri;

    private AddIngredientPresenter presenter;

    @Before
    public void setUp() {
        presenter = new AddIngredientPresenterImp(view, permissionsHelper, model);
        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));

        when(model.getName()).thenReturn(testName);
        when(model.getEnergyValue()).thenReturn(testEnergy);
        when(model.getImageUri()).thenReturn(testUri);
        when(view.showImage(any(Uri.class))).thenReturn(Observable.just(RxPicasso.PicassoEvent.SUCCESS));
        when(model.getEnergyDensityUnit()).thenReturn(testUnit);

        when(model.getLoading()).thenReturn(Observable.<Void>just(null));

        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>empty());
        when(view.getValueObservable()).thenReturn(Observable.<CharSequence>empty());
        when(model.canCreateIngredient(anyString(), anyString()))
                .thenReturn(Collections.<IngredientTypeCreateError>emptyList());
    }

    @Test
    public void testSave() {
        List<IngredientTypeCreateError> errors = Collections.emptyList();
        when(model.saveIntoDatabase()).thenReturn(Observable.just(errors));
        presenter.onClickedOnAction(R.id.action_save);

        verify(model).saveIntoDatabase();
        verify(view).showNameError(Optional.<Integer>absent());
        verify(view).showValueError(Optional.<Integer>absent());
        verify(view).setResultAndFinish();

        verifyNoMoreInteractions(view, model, permissionsHelper, testUri);
    }

    @Test
    public void testSaveError() throws Exception {
        when(model.saveIntoDatabase()).thenReturn(Observable.<List<IngredientTypeCreateError>>error(new TestError()));
        presenter.onClickedOnAction(R.id.action_save);

        verify(model).saveIntoDatabase();

        verifyNoMoreInteractions(view, model, permissionsHelper, testUri);
    }

    @Test
    public void testActionNotHandled() throws Exception {
        assertThat(presenter.onClickedOnAction(-1), equalTo(false));
    }

    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = Mockito.mock(Bundle.class);
        presenter.onSaveState(bundle);
        verify(model).onSaveState(bundle);

        verifyNoMoreInteractions(view, model, permissionsHelper, testUri);
    }

    @Test
    public void testOnStart() throws Exception {
        presenter.onStart();

        verifyStart();
        verifyNoMoreInteractions(view, model, permissionsHelper, testUri);
    }

    @Test
    public void testChangeName() throws Exception {
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just("Name"));

        presenter.onStart();

        verifyStart();
        verify(model).setName("Name");

        verifyNoMoreInteractions(view, model, permissionsHelper, testUri);
    }

    @Test
    public void testChangeValue() throws Exception {
        when(view.getValueObservable()).thenReturn(Observable.<CharSequence>just("2.66"));

        presenter.onStart();
        verifyStart();
        verify(model).setEnergyValue("2.66");
        verifyNoMoreInteractions(view, model, permissionsHelper, testUri);
    }

    @Test
    public void testNameEmptyError() throws Exception {
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just(""));
        when(view.getValueObservable()).thenReturn(Observable.<CharSequence>just("2.55"));
        when(model.canCreateIngredient(anyString(), anyString()))
                .thenReturn(Collections.singletonList(NO_NAME));

        presenter.onStart();
        verifyStart();

        verify(model).setName("");
        verify(model).setEnergyValue("2.55");
        verify(model).canCreateIngredient("", "2.55");
        verify(view).showNameError(eq(Optional.of(NO_NAME.getErrorResId())));
        verify(view).showValueError(eq(Optional.<Integer>absent()));
        verify(view).requestFocusToName();
        verifyNoMoreInteractions(view, model, permissionsHelper, testUri);
    }

    @Test
    public void testNoValueError() throws Exception {
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just("Name"));
        when(view.getValueObservable()).thenReturn(Observable.<CharSequence>just(""));
        when(model.canCreateIngredient(anyString(), anyString()))
                .thenReturn(Collections.singletonList(NO_VALUE));

        presenter.onStart();
        verifyStart();

        verify(model).setName("Name");
        verify(model).setEnergyValue("");
        verify(model).canCreateIngredient("Name", "");
        verify(view).showNameError(eq(Optional.<Integer>absent()));
        verify(view).showValueError(eq(Optional.of(NO_VALUE.getErrorResId())));
        verify(view).requestFocusToValue();
        verifyNoMoreInteractions(view, model, permissionsHelper, testUri);
    }

    private void verifyStart() {
        verify(model).getLoading();

        verify(model).getImageUri();
        verify(view).showImage(testUri);
        verify(model).setImageUri(testUri);

        verify(model).getName();
        verify(view).setName(testName);

        verify(model).getEnergyValue();
        verify(view).setEnergyDensityValue(testEnergy);

        verify(model).getEnergyDensityUnit();
        verify(view).setSelectedUnitName(testUnit);

        verify(view).getNameObservable();
        verify(view).getValueObservable();
    }
}