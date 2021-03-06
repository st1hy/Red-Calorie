package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailView;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailViewImpl;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.DetailImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.inject.HeaderImageView;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.quantifier.bundle.FragmentSavedState;
import com.google.common.base.Preconditions;

import org.parceler.Parcels;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.model.IngredientDetailModel.SAVED_INGREDIENT_MODEL;

@Module
public abstract class IngredientsDetailFragmentModule {

    @Binds
    @PerFragment
    public abstract IngredientDetailPresenter providePresenter(IngredientDetailPresenterImpl presenter);

    @Binds
    public abstract ImageHolderDelegate provideImageHolderDelegate(
            DetailImageHolderDelegate holderDelegate);

    @Binds
    public abstract IngredientDetailView provideView(IngredientDetailViewImpl detailView);

    @Provides
    @PerFragment
    @HeaderImageView
    public static ImageView provideImageView(IngredientDetailView view) {
        return view.getImageView();
    }

    @Provides
    @PerFragment
    public static Ingredient provideIngredient(@Nullable @FragmentSavedState Bundle savedState,
                                               Intent intent) {
        if (savedState != null) {
            return Parcels.unwrap(savedState.getParcelable(SAVED_INGREDIENT_MODEL));
        } else {
            Ingredient ingredient = Parcels.unwrap(
                    intent.getParcelableExtra(IngredientDetailActivity.EXTRA_INGREDIENT)
            );
            return Preconditions.checkNotNull(ingredient, "Missing ingredient!");
        }
    }

    @Provides
    public static long provideIngredientId(Intent intent) {
        return intent.getLongExtra(IngredientDetailActivity.EXTRA_INGREDIENT_ID_LONG, -1L);
    }
}
