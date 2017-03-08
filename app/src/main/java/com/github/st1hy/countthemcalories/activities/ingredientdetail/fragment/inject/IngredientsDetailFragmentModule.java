package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailView;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailViewImpl;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.WithoutPlaceholderImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.inject.quantifier.bundle.FragmentSavedState;
import com.github.st1hy.countthemcalories.inject.quantifier.context.AppContext;
import com.google.common.base.Preconditions;

import org.parceler.Parcels;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.model.IngredientDetailModel.SAVED_INGREDIENT_MODEL;

@Module(includes = {
        PermissionModule.class
})
public abstract class IngredientsDetailFragmentModule {

    @Binds
    @PerFragment
    public abstract IngredientDetailPresenter providePresenter(IngredientDetailPresenterImpl presenter);

    @Binds
    public abstract ImageHolderDelegate provideImageHolderDelegate(
            WithoutPlaceholderImageHolderDelegate holderDelegate);

    @Binds
    public abstract IngredientDetailView provideView(IngredientDetailViewImpl detailView);
    @Provides
    @PerFragment
    @Named("imageLoaderImageView")
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

    @Provides
    public static InputMethodManager inputMethodManager(@AppContext Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
