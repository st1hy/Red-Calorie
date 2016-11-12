package com.github.st1hy.countthemcalories.activities.ingredientdetail.inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreenImpl;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;

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
    public IngredientDetailFragment provideContent(FragmentManager fragmentManager, Bundle arguments) {
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
        return fragment;
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
    @PerActivity
    public IngredientDetailScreen ingredientDetailScreen() {
        return new IngredientDetailScreenImpl(activity);
    }
}
