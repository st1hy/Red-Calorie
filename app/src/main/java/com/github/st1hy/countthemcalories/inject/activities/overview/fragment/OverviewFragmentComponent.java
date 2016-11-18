package com.github.st1hy.countthemcalories.inject.activities.overview.fragment;

import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems.MealRowComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.fragment.OverviewFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.inject.core.RecyclerViewAdapterDelegateModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        OverviewFragmentModule.class,
        PermissionModule.class,
        RecyclerViewAdapterDelegateModule.class
})
public interface OverviewFragmentComponent extends MealRowComponentFactory {

    void inject(OverviewFragment fragment);
}
