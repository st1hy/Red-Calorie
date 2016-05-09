package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RxMockitoJUnitRunner.class)
public class AddMealPresenterTest {

    @Mock
    private AddMealView view;
    @Mock
    private PermissionsHelper permissionsHelper;
    @Mock
    private IngredientsAdapter ingredientsAdapter;
    @Mock
    private AddMealModel model;
    private AddMealPresenter presenter;

    @Before
    public void setup() {
        presenter = new AddMealPresenterImp(view, permissionsHelper, ingredientsAdapter, model);
        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));
    }

    @Test
    public void testSave() {
        presenter.onClickedOnAction(R.id.action_save);
        verify(view, only()).openOverviewActivity();
    }

}
