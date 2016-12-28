package com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import javax.inject.Provider;

import rx.Observable;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IngredientsPresenterTest {

    @Mock
    IngredientsView view;
    @Mock
    Provider<SearchResult> lastSearchResult;

    IngredientsPresenterImpl presenter;

    @Before
    public void setUp() throws Exception {
        when(lastSearchResult.get()).thenReturn(SearchResult.EMPTY);
        presenter = new IngredientsPresenterImpl(view, lastSearchResult);
    }

    @Test
    public void testStart() throws Exception {
        when(view.getOnAddIngredientClickedObservable()).thenReturn(Observable.<Void>empty());

        presenter.onStart();

        verify(view).getOnAddIngredientClickedObservable();
        testVerifyNoMoreInteractions();
    }

    private void testVerifyNoMoreInteractions() {
        verifyNoMoreInteractions(view, lastSearchResult);
    }


    @Test
    public void testAddIngredientClicked() throws Exception {
        when(view.getOnAddIngredientClickedObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();
        verify(view).getOnAddIngredientClickedObservable();
        verify(view).selectIngredientType();
        testVerifyNoMoreInteractions();
    }


    @Test
    public void testOpenNewIngredients() throws Exception {
        presenter.onSelectedNewIngredientType(AddIngredientType.MEAL);
        verify(view).openNewIngredientScreen(AddIngredientType.MEAL, "");
        verify(lastSearchResult).get();

        when(lastSearchResult.get()).thenReturn(new SearchResult("test", Collections.<String>emptyList()));
        presenter.onSelectedNewIngredientType(AddIngredientType.DRINK);
        verify(view).openNewIngredientScreen(AddIngredientType.DRINK, "test");
        verify(lastSearchResult, times(2)).get();

        testVerifyNoMoreInteractions();
    }

}