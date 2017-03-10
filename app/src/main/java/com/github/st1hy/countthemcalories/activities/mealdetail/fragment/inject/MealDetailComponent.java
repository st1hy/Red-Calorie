package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject;

import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.MealDetailFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        MealDetailsModule.class,
})
public interface MealDetailComponent {

    void inject(MealDetailFragment fragment);
}
