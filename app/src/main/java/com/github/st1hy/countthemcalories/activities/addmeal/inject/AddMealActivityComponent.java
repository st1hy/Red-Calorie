package com.github.st1hy.countthemcalories.activities.addmeal.inject;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.dialog.DialogViewModule;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = {AddMealActivityModule.class, DialogViewModule.class},
        dependencies = ApplicationComponent.class)
public interface AddMealActivityComponent {

    void inject(AddMealActivity activity);

    AddMealFragmentComponent addMealFragmentComponent(AddMealFragmentModule addMealFragmentModule);

}
