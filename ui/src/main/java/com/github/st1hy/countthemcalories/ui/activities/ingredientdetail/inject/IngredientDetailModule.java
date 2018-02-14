package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.inject;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.view.IngredientDetailScreenImpl;
import com.github.st1hy.countthemcalories.ui.core.FragmentLocation;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentTransactionModule;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module(includes = FragmentTransactionModule.class)
public abstract class IngredientDetailModule {

    private static final String INGREDIENT_DETAIL_CONTENT = "ingredient detail content";

    @Binds
    public abstract IngredientDetailScreen ingredientDetailScreen(IngredientDetailScreenImpl screen);

    @Provides
    @IntoMap
    @StringKey(INGREDIENT_DETAIL_CONTENT)
    @Reusable
    public static FragmentLocation ingredientDetailContent(IngredientDetailComponent component) {
        return new FragmentLocation.Builder<>(IngredientDetailFragment.class, INGREDIENT_DETAIL_CONTENT)
                .setViewRootId(R.id.ingredient_detail_content)
                .setInjector(fragment -> fragment.setComponentFactory(component))
                .build();
    }

}
