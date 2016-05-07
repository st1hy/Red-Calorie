package com.github.st1hy.countthemcalories.activities.overview.presenter;

import com.github.st1hy.countthemcalories.activities.overview.view.OverviewView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

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
    public void testOpenAddMeal() {
        presenter.onAddMealButtonClicked();
        verify(view, only()).openAddMealScreen();
    }
}
