package com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.inject;

import com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.MealDetailFragment;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        MealDetailsModule.class,
})
public interface MealDetailComponent {

    void inject(MealDetailFragment fragment);
}
