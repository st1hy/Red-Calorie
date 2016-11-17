package com.github.st1hy.countthemcalories.inject.activities.addmeal;

import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.AddMealFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.core.ToolbarNavigateBackModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        AddMealActivityModule.class,
        ToolbarNavigateBackModule.class
})
public interface AddMealActivityComponent extends AddMealFragmentComponentFactory {

    void inject(AddMealActivity activity);

}
