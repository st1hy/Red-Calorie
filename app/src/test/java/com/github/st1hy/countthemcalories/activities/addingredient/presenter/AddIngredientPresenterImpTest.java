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
import com.google.common.base.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import rx.Observable;

import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.NO_VALUE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RxMockitoJUnitRunner.class)
public class AddIngredientPresenterImpTest {

    @Mock
    private AddIngredientView view;
    @Mock
    private PermissionsHelper permissionsHelper;
    @Mock
    private AddIngredientModel model;
    private AddIngredientPresenter presenter;

    @Before
    public void setup() {
        presenter = new AddIngredientPresenterImp(view, permissionsHelper, model);
        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));
    }

    @Test
    public void testSave() {
        List<IngredientTypeCreateError> errors = Collections.emptyList();
        when(model.insertIntoDatabase()).thenReturn(Observable.just(errors));
        presenter.onClickedOnAction(R.id.action_save);

        verify(model).insertIntoDatabase();
        verify(view).setResultAndFinish();
    }

    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = Mockito.mock(Bundle.class);
        presenter.onSaveState(bundle);
        verify(model).onSaveState(bundle);
    }

    @Test
    public void testOnStart() throws Exception {
        final String testUnit = "test unit";
        final String testName = "test name";
        final String testEnergy = "test energy";
        final Uri testUri = Mockito.mock(Uri.class);

        when(model.getName()).thenReturn(testName);
        when(model.getEnergyValue()).thenReturn(testEnergy);
        when(model.getImageUri()).thenReturn(testUri);
        when(model.getEnergyDensityUnit()).thenReturn(testUnit);
        when(view.showImage(any(Uri.class))).thenReturn(Observable.just(RxPicasso.PicassoEvent.SUCCESS));
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just("Name"));
        when(view.getValueObservable()).thenReturn(Observable.<CharSequence>just(""));
        when(model.canCreateIngredient("Name", "")).thenReturn(Collections.singletonList(NO_VALUE));

        presenter.onStart();

        verify(model).getName();
        verify(model).getEnergyValue();
        verify(model).getImageUri();
        verify(view).setName(testName);
        verify(view).setEnergyDensityValue(testEnergy);
        verify(model).getEnergyDensityUnit();
        verify(view).setSelectedUnitName(testUnit);
        verify(view).showImage(testUri);
        verify(model).setImageUri(testUri);
        verify(view).getNameObservable();
        verify(model).setName("Name");
        verify(view).getValueObservable();
        verify(model).setEnergyValue("");
        verify(model).canCreateIngredient(anyString(), anyString());
        verify(view).showNameError(Optional.<Integer>absent());
        verify(view).showValueError(eq(Optional.of(NO_VALUE.getErrorResId())));
        verify(view).requestFocusToValue();
        verifyNoMoreInteractions(view, model);
    }
}