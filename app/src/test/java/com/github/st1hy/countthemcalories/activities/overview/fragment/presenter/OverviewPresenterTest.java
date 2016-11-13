package com.github.st1hy.countthemcalories.activities.overview.fragment.presenter;

import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OverviewPresenterTest {

    @Mock
    private OverviewView view;
    @Mock
    private MealsAdapter adapter;
    private OverviewPresenterImp presenter;

    @Before
    public void setup() {
        presenter = new OverviewPresenterImp(view, adapter);
    }

    @Test
    public void testStart() throws Exception {
        when(view.getAddNewMealObservable()).thenReturn(Observable.<Void>empty());

        presenter.onStart();

        verify(adapter).onStart();
        verify(view).getAddNewMealObservable();
    }

    @Test
    public void testAddMeal() throws Exception {
        when(view.getAddNewMealObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();

        verify(adapter).onStart();
        verify(view).getAddNewMealObservable();

        verify(view).addNewMeal();
    }

    @Test
    public void testStop() throws Exception {
        presenter.onStop();

        assertThat(presenter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(presenter.subscriptions.hasSubscriptions(), equalTo(false));
        verify(adapter).onStop();
    }
}
