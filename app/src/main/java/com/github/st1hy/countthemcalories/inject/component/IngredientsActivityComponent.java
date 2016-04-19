package com.github.st1hy.countthemcalories.inject.component;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.github.st1hy.countthemcalories.IngredientsActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.module.IngredientsActivityModule;

import dagger.Component;

@PerActivity
@Component(modules = IngredientsActivityModule.class, dependencies = ApplicationComponent.class)
public interface IngredientsActivityComponent {

    @PerActivity
    Activity getActivity();

    @PerActivity
    DrawerLayout getDrawer();

    @PerActivity
    NavigationView getNavigationView();

    @PerActivity
    Toolbar getToolbar();

    void inject(IngredientsActivity activity);
}
