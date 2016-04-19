package com.github.st1hy.countthemcalories.activities.ingredients.inject;

import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = IngredientsActivityModule.class, dependencies = ApplicationComponent.class)
public interface IngredientsActivityComponent {

    void inject(IngredientsActivity activity);
}
