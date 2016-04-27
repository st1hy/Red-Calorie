package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.support.annotation.IdRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.ui.Selection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IngredientsPresenterImpTest {

    @Mock
    private IngredientsView view;
    private IngredientsPresenter presenter;

    @Before
    public void setup() {
        presenter = new IngredientsPresenterImp(view);
    }

    @Test
    public void testPressedBackDrawerOpen() throws Exception {
        when(view.isDrawerOpen()).thenReturn(true);

        presenter.onBackPressed();

        verify(view, times(1)).isDrawerOpen();
        verify(view, times(1)).closeDrawer();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testPressedBack() throws Exception {
        when(view.isDrawerOpen()).thenReturn(false);

        presenter.onBackPressed();

        verify(view, times(1)).isDrawerOpen();
        verify(view, times(1)).invokeActionBack();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testStart() throws Exception {
        presenter.onStart();

        verify(view, only()).setMenuItemSelection(anyInt(), any(Selection.class));
    }

    @Test
    public void testOnNavigationSelected() throws Exception {
        @IdRes final int menuItem = -1;
        presenter.onNavigationItemSelected(menuItem);

        verify(view).setMenuItemSelection(menuItem, Selection.SELECTED);
        verify(view).closeDrawer();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testOnOverviewSelectedInMenu() throws Exception {
        @IdRes final int menuItem = R.id.nav_overview;
        presenter.onNavigationItemSelected(menuItem);

        verify(view, times(1)).openOverviewScreen();
        verify(view).setMenuItemSelection(menuItem, Selection.SELECTED);
        verify(view).closeDrawer();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testClickedOnAction() throws Exception {
        @IdRes final int actionId = -1;
        boolean isHandled = presenter.onClickedOnAction(actionId);
        verifyZeroInteractions(view);

        assertFalse(isHandled);
    }

    @Test
    public void testOpenNewIngredients() throws Exception {
        presenter.onAddNewIngredientClicked();

        verify(view, only()).openNewIngredientScreen();
    }

    @Test
    public void testOpenSettingsUsingMenu() throws Exception {
        @IdRes final int menuItem = R.id.nav_settings;
        presenter.onNavigationItemSelected(menuItem);

        verify(view, times(1)).openSettingsScreen();
        verify(view).setMenuItemSelection(menuItem, Selection.SELECTED);
        verify(view).closeDrawer();
        verifyNoMoreInteractions(view);

    }
}