package com.github.st1hy.countthemcalories.ui.activities.mealdetail.inject;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.MealDetailFragment;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.view.MealDetailScreen;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.view.MealDetailScreenImpl;
import com.github.st1hy.countthemcalories.ui.core.FragmentLocation;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentTransactionModule;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module(includes = FragmentTransactionModule.class)
public abstract class MealDetailActivityModel {

    private static final String FRAGMENT_TAG = "meal detail content";

    @Binds
    public abstract MealDetailScreen mealDetailScreen(MealDetailScreenImpl screen);


    @Provides
    @Reusable
    @IntoMap
    @StringKey(FRAGMENT_TAG)
    public static FragmentLocation mealDetailFragmentLocation(MealDetailActivityComponent component) {
        return new FragmentLocation.Builder<>(MealDetailFragment.class, FRAGMENT_TAG)
                .setViewRootId(R.id.meal_detail_content_root)
                .setInjector(fragment -> fragment.setComponentFactory(component))
                .build();
    }
}
