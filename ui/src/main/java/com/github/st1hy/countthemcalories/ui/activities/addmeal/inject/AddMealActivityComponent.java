package com.github.st1hy.countthemcalories.ui.activities.addmeal.inject;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.inject.AddMealFragmentComponentFactory;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        AddMealActivityModule.class,
})
public interface AddMealActivityComponent extends AddMealFragmentComponentFactory {

    void inject(AddMealActivity activity);

}
