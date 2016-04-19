package com.github.st1hy.countthemcalories.activities.overview.presenter;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OverviewPresenterTest {

    @Mock
    private OverviewView view;
    private OverviewPresenter presenter;

    @Before
    public void setup() {
        presenter = new OverviewPresenterImp(view);
    }

    @Test
    public void testSetup() {
        assertThat(view, notNullValue());
        assertThat(presenter, notNullValue());
    }

    @Test
    public void testClosingDrawerUsingBackButton() {
        when(view.isDrawerOpen()).thenReturn(true);

        presenter.onBackPressed();

        verify(view).isDrawerOpen();
        verify(view).closeDrawer();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testBackButton() {
        when(view.isDrawerOpen()).thenReturn(false);

        presenter.onBackPressed();

        verify(view).isDrawerOpen();
        verify(view).invokeActionBack();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testOpenAddMeal() {
        presenter.onAddMealButtonClicked();
        verify(view, only()).openAddMealScreen();
    }

    @Test
    public void canOpenIngredients() {
        presenter.onNavigationItemSelected(R.id.nav_ingredients);
        verify(view).openIngredientsScreen();
    }

}
