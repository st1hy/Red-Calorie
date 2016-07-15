package com.github.st1hy.countthemcalories.activities.ingredientdetail.inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailActivity;

import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_TEMPLATE_PARCEL;

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
                    .add(R.id.ingredient_detail_root, fragment, tag)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commit();
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
        bundle.putParcelable(EXTRA_INGREDIENT_TEMPLATE_PARCEL,
                intent.getParcelableExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL));
        bundle.putLong(EXTRA_INGREDIENT_ID_LONG,
                intent.getLongExtra(EXTRA_INGREDIENT_ID_LONG, -1L));
        bundle.putString(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL,
                intent.getStringExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL));
        return bundle;
    }
}
