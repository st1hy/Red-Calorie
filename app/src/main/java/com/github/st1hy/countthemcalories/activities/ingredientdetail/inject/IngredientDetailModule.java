package com.github.st1hy.countthemcalories.activities.ingredientdetail.inject;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreenImpl;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class IngredientDetailModule {

    private static final String INGREDIENT_DETAIL_CONTENT = "ingredient detail content";

    @Binds
    public abstract IngredientDetailScreen ingredientDetailScreen(IngredientDetailScreenImpl screen);

    @Provides
    public static IngredientDetailFragment provideContent(FragmentManager fragmentManager,
                                                          IngredientDetailComponent component) {

        IngredientDetailFragment fragment = (IngredientDetailFragment) fragmentManager
                .findFragmentByTag(INGREDIENT_DETAIL_CONTENT);
        if (fragment == null) {
            fragment = new IngredientDetailFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.ingredient_detail_content, fragment, INGREDIENT_DETAIL_CONTENT)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(component);
        return fragment;
    }

}
