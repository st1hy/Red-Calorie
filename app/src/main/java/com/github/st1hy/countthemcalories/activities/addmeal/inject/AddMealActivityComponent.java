package com.github.st1hy.countthemcalories.activities.addmeal.inject;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.core.dialog.DialogViewModule;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {AddMealActivityModule.class, DialogViewModule.class})
public interface AddMealActivityComponent extends AddMealFragmentComponentFactory {

    void inject(AddMealActivity activity);

}
