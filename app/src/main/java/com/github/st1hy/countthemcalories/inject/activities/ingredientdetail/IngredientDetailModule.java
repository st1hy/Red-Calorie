package com.github.st1hy.countthemcalories.inject.activities.ingredientdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.IngredientDetailFragment;

import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientsDetailFragmentModule.EXTRA_INGREDIENT;
import static com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;

@Module(includes = IngredientDetailBindings.class)
public class IngredientDetailModule {

    private final IngredientDetailActivity activity;
    private static final String INGREDIENT_DETAIL_CONTENT = "ingredient detail content";

    public IngredientDetailModule(@NonNull IngredientDetailActivity activity) {
        this.activity = activity;
    }

    @Provides
    public static Intent provideIntent(Activity activity) {
        return activity.getIntent();
    }

    @Provides
    public static IngredientDetailFragment provideContent(FragmentManager fragmentManager,
                                                          Bundle arguments,
                                                          IngredientDetailComponent component) {

        IngredientDetailFragment fragment = (IngredientDetailFragment) fragmentManager
                .findFragmentByTag(INGREDIENT_DETAIL_CONTENT);
        if (fragment == null) {
            fragment = new IngredientDetailFragment();
            fragment.setArguments(arguments);

            fragmentManager.beginTransaction()
                    .add(R.id.ingredient_detail_content, fragment, INGREDIENT_DETAIL_CONTENT)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(component);
        return fragment;
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
    }

    @Provides
    public static FragmentManager provideFragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @Provides
    public static Bundle provideArguments(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_INGREDIENT,
                intent.getParcelableExtra(EXTRA_INGREDIENT));
        bundle.putLong(EXTRA_INGREDIENT_ID_LONG,
                intent.getLongExtra(EXTRA_INGREDIENT_ID_LONG, -1L));
        return bundle;
    }

}
