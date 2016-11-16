package com.github.st1hy.countthemcalories.inject.activities.ingredientdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreenImpl;

import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientsDetailFragmentModule.EXTRA_INGREDIENT;
import static com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;

@Module
public class IngredientDetailModule {

    private final IngredientDetailActivity activity;

    public IngredientDetailModule(@NonNull IngredientDetailActivity activity) {
        this.activity = activity;
    }

    @Provides
    public Intent provideIntent() {
        return activity.getIntent();
    }

    @Provides
    public IngredientDetailFragment provideContent(FragmentManager fragmentManager,
                                                   Bundle arguments,
                                                   IngredientDetailComponent component) {
        final String tag = "ingredient detail content";

        IngredientDetailFragment fragment = (IngredientDetailFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new IngredientDetailFragment();
            fragment.setArguments(arguments);

            fragmentManager.beginTransaction()
                    .add(R.id.ingredient_detail_content, fragment, tag)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        fragment.setComponentFactory(component);
        return fragment;
    }

    @Provides
    public Activity activity() {
        return activity;
    }

    @Provides
    public FragmentManager provideFragmentManager() {
        return activity.getSupportFragmentManager();
    }

    @Provides
    public Bundle provideArguments(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_INGREDIENT,
                intent.getParcelableExtra(EXTRA_INGREDIENT));
        bundle.putLong(EXTRA_INGREDIENT_ID_LONG,
                intent.getLongExtra(EXTRA_INGREDIENT_ID_LONG, -1L));
        return bundle;
    }

    @Provides
    public IngredientDetailScreen ingredientDetailScreen(IngredientDetailScreenImpl screen) {
        return screen;
    }
}
