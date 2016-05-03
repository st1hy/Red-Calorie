package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientView;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import rx.Observable;
import rx.functions.Func1;

import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_ML;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
        when(model.canCreateIngredient()).thenReturn(true);
        when(model.insertIntoDatabase()).thenReturn(Observable.<Void>just(null));
        presenter.onClickedOnAction(R.id.action_save);

        verify(model).insertIntoDatabase();
        verify(view).setResultAndFinish();
    }

    @Test
    public void testSelectUnit() throws Exception {
        when(view.showAlertDialog(anyInt(), any(String[].class)))
                .thenReturn(Observable.just(1));
        when(model.getUnitSelection()).thenReturn(GravimetricEnergyDensityUnit.values());

        presenter.onSelectUnitClicked();

        verify(model).getUnitSelectionOptions();
        verify(model).getSelectUnitDialogTitle();
        verify(view).showAlertDialog(anyInt(), any(String[].class));
        verify(model).getUnitSelection();
        verify(model).setUnit(GravimetricEnergyDensityUnit.values()[1]);
        verifyNoMoreInteractions(view, permissionsHelper, model);
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

        when(model.getUnitObservable()).thenReturn(Observable.<EnergyDensityUnit>just(KCAL_AT_ML));
        when(model.unitAsString()).thenReturn(new Func1<EnergyDensityUnit, String>() {
            @Override
            public String call(EnergyDensityUnit energyDensityUnit) {
                return testUnit;
            }
        });
        when(model.getName()).thenReturn(testName);
        when(model.getEnergyValue()).thenReturn(testEnergy);
        when(model.getImageUri()).thenReturn(testUri);
        when(view.showImage(any(Uri.class))).thenReturn(Observable.just(RxPicasso.PicassoEvent.SUCCESS));
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just("Name"));
        when(view.getValueObservable()).thenReturn(Observable.<CharSequence>just("Value"));

        presenter.onStart();

        verify(model).getUnitObservable();
        verify(model).unitAsString();
        verify(model).getName();
        verify(model).getEnergyValue();
        verify(model).getImageUri();
        verify(view).setName(testName);
        verify(view).setEnergyDensityValue(testEnergy);
        verify(view).setSelectedUnitName(testUnit);
        verify(view).showImage(testUri);
        verify(model).setImageUri(testUri);
        verify(view).getNameObservable();
        verify(model).setName("Name");
        verify(view).getValueObservable();
        verify(model).setEnergyValue("Value");
        verifyNoMoreInteractions(view, model);
    }
}