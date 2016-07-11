package com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter;

import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IngredientsPresenterTest {

    @Mock
    IngredientsView view;

    IngredientsPresenterImpl presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new IngredientsPresenterImpl(view);
    }

    @Test
    public void testStart() throws Exception {
        when(view.getOnAddIngredientClickedObservable()).thenReturn(Observable.<Void>empty());

        presenter.onStart();

        verify(view).getOnAddIngredientClickedObservable();
        verifyNoMoreInteractions(view);
    }


    @Test
    public void testAddIngredientClicked() throws Exception {
        when(view.getOnAddIngredientClickedObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();
        verify(view).getOnAddIngredientClickedObservable();
        verify(view).selectIngredientType();
        verifyNoMoreInteractions(view);
    }


    @Test
    public void testOpenNewIngredients() throws Exception {
        presenter.onSelectedNewIngredientType(AddIngredientType.MEAL);
        verify(view).openNewIngredientScreen(AddIngredientType.MEAL);

        presenter.onSelectedNewIngredientType(AddIngredientType.DRINK);
        verify(view).openNewIngredientScreen(AddIngredientType.DRINK);

        verifyNoMoreInteractions(view);
    }

}