package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    private AddMealPresenterImp presenter;

    @Before
    public void setup() {
        presenter = new AddMealPresenterImp(view, permissionsHelper, ingredientsAdapter, model);
        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));
    }

    @Test
    public void testSave() {
        presenter.onClickedOnAction(R.id.action_save);
        verify(view).openOverviewActivity();
        verifyNoMoreInteractions(view, permissionsHelper, ingredientsAdapter, model);
    }

    @Test
    public void testOnStart() throws Exception {
        final Uri uri = mock(Uri.class);

        when(model.getName()).thenReturn("Name");
        when(model.getImageUri()).thenReturn(uri);

        when(view.showImage(uri)).thenReturn(Observable.just(RxPicasso.PicassoEvent.SUCCESS));
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just("Name2"));
        when(view.getAddIngredientObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();
        verify(model).getImageUri();
        verify(view).showImage(uri);
        verify(model).setImageUri(uri);
        verify(model).getName();
        verify(view).setName("Name");
        verify(model).setName("Name2");
        verify(view).openAddIngredient();
        verify(view).getAddIngredientObservable();
        verify(view).getNameObservable();
        verify(ingredientsAdapter).onStart();

        verifyNoMoreInteractions(view, permissionsHelper, ingredientsAdapter, model);
    }

    @Test
    public void testOnStop() throws Exception {
        presenter.onStop();
        verify(ingredientsAdapter).onStop();
        verifyNoMoreInteractions(view, permissionsHelper, ingredientsAdapter, model);
    }

    @Test
    public void testOnSaveInstance() throws Exception {
        Bundle bundle = new Bundle();
        presenter.onSaveState(bundle);

        verify(model).onSaveState(bundle);
        verifyNoMoreInteractions(view, permissionsHelper, ingredientsAdapter, model);
    }
}
