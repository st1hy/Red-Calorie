package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.SelectIngredientTypeActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.state.Selection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem.INGREDIENTS;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IngredientsPresenterTest {

    @Mock
    IngredientsView view;
    @Mock
    IngredientsDaoAdapter daoAdapter;
    IngredientsPresenterImpl presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new IngredientsPresenterImpl(view, daoAdapter, searchBar);
    }

    @Test
    public void testStart() throws Exception {
        when(view.getOnAddIngredientClickedObservable()).thenReturn(Observable.<Void>empty());

        presenter.onStart();

        verify(view).getOnAddIngredientClickedObservable();
        verify(daoAdapter).onStart();
        verify(view).setMenuItemSelection(eq(INGREDIENTS.getMenuItemId()), any(Selection.class));
        verify(view).showNavigationAsUp();
        verify(daoAdapter).onStart();
        verifyNoMoreInteractions(view, daoAdapter);
    }


    @Test
    public void testOpenNewIngredientsWithMealIntent() throws Exception {
        when(view.getOnAddIngredientClickedObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();
        verify(view).selectIngredientType();
        verify(view).setMenuItemSelection(eq(INGREDIENTS.getMenuItemId()), any(Selection.class));
        verify(view).getOnAddIngredientClickedObservable();
        verify(view).showNavigationAsUp();
        verify(daoAdapter).onStart();
        verifyNoMoreInteractions(view, daoAdapter);
        presenter.onSelectIngredientTypeResult(SelectIngredientTypeActivity.RESULT_MEAL);

        verify(view).openNewIngredientScreen(AddIngredientActivity.ACTION_CREATE_MEAL);
        verifyNoMoreInteractions(view, daoAdapter);
    }



    @Test
    public void testOpenNewIngredientsWithDrinkIntent() throws Exception {
        when(view.getOnAddIngredientClickedObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();
        verify(view).selectIngredientType();
        verify(view).setMenuItemSelection(eq(INGREDIENTS.getMenuItemId()), any(Selection.class));
        verify(view).getOnAddIngredientClickedObservable();
        verify(view).showNavigationAsUp();
        verify(daoAdapter).onStart();
        verifyNoMoreInteractions(view, daoAdapter);
        presenter.onSelectIngredientTypeResult(SelectIngredientTypeActivity.RESULT_DRINK);

        verify(view).openNewIngredientScreen(AddIngredientActivity.ACTION_CREATE_DRINK);
        verifyNoMoreInteractions(view, daoAdapter);
    }


    @Test
    public void testOnStop() throws Exception {
        presenter.onStop();

        verify(daoAdapter).onStop();
        verifyNoMoreInteractions(view, daoAdapter);

        assertFalse(presenter.subscriptions.hasSubscriptions());
        assertFalse(presenter.subscriptions.isUnsubscribed());
    }
}