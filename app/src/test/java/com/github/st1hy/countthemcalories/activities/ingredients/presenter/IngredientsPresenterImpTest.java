package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.support.annotation.IdRes;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientsActivityModel;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.state.Selection;
import com.squareup.picasso.Picasso;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class IngredientsPresenterImpTest {

    private IngredientsView view;
    private IngredientsActivityModel activityModel;
    private IngredientTypesModel model;
    private SettingsModel settingsModel;
    private Picasso picasso;
    private IngredientsPresenter presenter;

    @Before
    public void setup() {
        view = Mockito.mock(IngredientsView.class);
        activityModel= Mockito.mock(IngredientsActivityModel.class);
        model = Mockito.mock(IngredientTypesModel.class);
        settingsModel = Mockito.mock(SettingsModel.class);
        picasso = Mockito.mock(Picasso.class);
        presenter = new IngredientsPresenterImp(view, activityModel, model, settingsModel, picasso);
    }

    @Test
    public void testPressedBackDrawerOpen() throws Exception {
        when(view.isDrawerOpen()).thenReturn(true);

        presenter.onBackPressed();

        verify(view, times(1)).isDrawerOpen();
        verify(view, times(1)).closeDrawer();
        verifyNoMoreInteractions(view, activityModel, model, settingsModel, picasso);
    }

    @Test
    public void testPressedBack() throws Exception {
        when(view.isDrawerOpen()).thenReturn(false);

        presenter.onBackPressed();

        verify(view, times(1)).isDrawerOpen();
        verify(view, times(1)).invokeActionBack();
        verifyNoMoreInteractions(view, activityModel, model, settingsModel, picasso);
    }

    @Test
    public void testStart() throws Exception {
        when(view.getOnAddIngredientClickedObservable()).thenReturn(Observable.<Void>empty());

        presenter.onStart();

        verify(view).setMenuItemSelection(anyInt(), any(Selection.class));
        verify(view).getOnAddIngredientClickedObservable();
        verifyNoMoreInteractions(view, activityModel, model, settingsModel, picasso);
    }

    @Test
    public void testOnNavigationSelected() throws Exception {
        @IdRes final int menuItem = -1;
        presenter.onNavigationItemSelected(menuItem);

        verify(view).setMenuItemSelection(menuItem, Selection.SELECTED);
        verify(view).closeDrawer();
        verifyNoMoreInteractions(view, activityModel, model, settingsModel, picasso);
    }

    @Test
    public void testOnOverviewSelectedInMenu() throws Exception {
        @IdRes final int menuItem = R.id.nav_overview;
        presenter.onNavigationItemSelected(menuItem);

        verify(view, times(1)).openOverviewScreen();
        verify(view).setMenuItemSelection(menuItem, Selection.SELECTED);
        verify(view).closeDrawer();
        verifyNoMoreInteractions(view, activityModel, model, settingsModel, picasso);
    }

    @Test
    public void testClickedOnAction() throws Exception {
        @IdRes final int actionId = -1;
        boolean isHandled = presenter.onClickedOnAction(actionId);

        assertFalse(isHandled);

        verifyNoMoreInteractions(view, activityModel, model, settingsModel, picasso);
    }

    @Test
    public void testOpenNewIngredients() throws Exception {
        when(view.getOnAddIngredientClickedObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();

        verify(view).openNewIngredientScreen();
        verify(view).setMenuItemSelection(anyInt(), any(Selection.class));
        verify(view).getOnAddIngredientClickedObservable();
        verifyNoMoreInteractions(view, activityModel, model, settingsModel, picasso);
    }

    @Test
    public void testOpenSettingsUsingMenu() throws Exception {
        @IdRes final int menuItem = R.id.nav_settings;
        presenter.onNavigationItemSelected(menuItem);

        verify(view, times(1)).openSettingsScreen();
        verify(view).setMenuItemSelection(menuItem, Selection.SELECTED);
        verify(view).closeDrawer();
        verifyNoMoreInteractions(view, activityModel, model, settingsModel, picasso);

    }
}