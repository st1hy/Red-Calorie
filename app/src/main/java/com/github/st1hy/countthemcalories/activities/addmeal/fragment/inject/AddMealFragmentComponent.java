package com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealFragment;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Component;

@PerFragment
@Component(modules = AddMealFragmentModule.class, dependencies = ApplicationComponent.class)
public interface AddMealFragmentComponent {

    void inject(AddMealFragment fragment);
}
