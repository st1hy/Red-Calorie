package com.github.st1hy.countthemcalories.inject.component;

import android.app.Activity;
import android.support.v7.widget.Toolbar;

import com.github.st1hy.countthemcalories.AddMealActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.module.AddMealActivityModule;

import dagger.Component;

@PerActivity
@Component(modules = AddMealActivityModule.class, dependencies = ApplicationComponent.class)
public interface AddMealActivityComponent {
    @PerActivity
    Activity getActivity();

    @PerActivity
    Toolbar getToolbar();

    void inject(AddMealActivity activity);

}
