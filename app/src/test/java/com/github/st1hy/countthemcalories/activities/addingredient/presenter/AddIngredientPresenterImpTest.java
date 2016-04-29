package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientView;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
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
        presenter.onClickedOnAction(R.id.action_save);
        verify(view, only()).openIngredientsScreen();
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
}