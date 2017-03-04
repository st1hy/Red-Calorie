package com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailView;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.google.common.base.Preconditions;

import org.parceler.Parcels;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.model.IngredientDetailModel.SAVED_INGREDIENT_MODEL;

@Module(includes = IngredientsDetailFragmentBindings.class)
public class IngredientsDetailFragmentModule {

    public static final String EXTRA_INGREDIENT = "ingredient details ingredient";
    public static final String EXTRA_INGREDIENT_ID_LONG = "ingredient details extra id long";

    private final IngredientDetailFragment fragment;
    @Nullable
    private final Bundle savedState;

    public IngredientsDetailFragmentModule(@NonNull IngredientDetailFragment fragment,
                                           @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @Named("savedState")
    @Nullable
    public Bundle provideSavedState() {
        return savedState;
    }

    @Provides
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    @Named("arguments")
    public Bundle provideArguments() {
        return Preconditions.checkNotNull(fragment.getArguments());
    }

    @Provides
    @PerFragment
    @Named("imageLoaderImageView")
    public static ImageView provideImageView(IngredientDetailView view) {
        return view.getImageView();
    }

    @Provides
    @PerFragment
    public static Ingredient provideIngredient(@Nullable @Named("savedState") Bundle savedState,
                                               @Named("arguments") Bundle arguments) {
        if (savedState != null) {
            return Parcels.unwrap(savedState.getParcelable(SAVED_INGREDIENT_MODEL));
        } else {
            Ingredient ingredient = Parcels.unwrap(arguments.getParcelable(EXTRA_INGREDIENT));
            Preconditions.checkNotNull(ingredient, "Missing ingredient!");
            return ingredient;
        }
    }

    @Provides
    public static long provideIngredientId(@Named("arguments") Bundle arguments) {
        return arguments.getLong(EXTRA_INGREDIENT_ID_LONG, -1L);
    }

    @Provides
    public static InputMethodManager inputMethodManager(@Named("appContext") Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
