package com.github.st1hy.countthemcalories.core.drawer.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractDrawerPresenterTest {

    @Mock
    private DrawerView view;
    private DrawerPresenter presenter;
    private DrawerMenuItem currentItem = DrawerMenuItem.OVERVIEW;

    @Before
    public void setup() {
        presenter = new DrawerPresenterImpl(view, currentItem) {
            @NonNull
            @Override
            protected DrawerMenuItem currentItem() {
                return currentItem;
            }
        };
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
    public void canOpenIngredients() {
        presenter.onNavigationItemSelected(R.id.nav_ingredients);
        verify(view).openDrawerActivity(DrawerMenuItem.INGREDIENTS);
    }

    @Test
    public void canOpenSettings() {
        presenter.onNavigationItemSelected(R.id.nav_settings);
        verify(view).openDrawerActivity(DrawerMenuItem.SETTINGS);
    }


    @Test
    public void canOpenCategories() {
        presenter.onNavigationItemSelected(R.id.nav_tags);
        verify(view).openDrawerActivity(DrawerMenuItem.CATEGORIES);
    }

    @Test
    public void canOpenOverview() {
        currentItem = DrawerMenuItem.INGREDIENTS;
        presenter.onNavigationItemSelected(R.id.nav_overview);
        verify(view).openDrawerActivity(DrawerMenuItem.OVERVIEW);
    }

    @Test
    public void canIgnoreOpenMenuWhenItsTheCurrent() {
        currentItem = DrawerMenuItem.OVERVIEW;
        presenter.onNavigationItemSelected(R.id.nav_overview);
        currentItem = DrawerMenuItem.INGREDIENTS;
        presenter.onNavigationItemSelected(R.id.nav_ingredients);
        currentItem = DrawerMenuItem.SETTINGS;
        presenter.onNavigationItemSelected(R.id.nav_settings);
        currentItem = DrawerMenuItem.CATEGORIES;
        presenter.onNavigationItemSelected(R.id.nav_tags);
        verify(view, times(4)).closeDrawer();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testClickedOnSettings() throws Exception {
        presenter.onClickedOnAction(R.id.action_settings);
        verify(view, only()).openDrawerActivity(DrawerMenuItem.SETTINGS);
    }
}