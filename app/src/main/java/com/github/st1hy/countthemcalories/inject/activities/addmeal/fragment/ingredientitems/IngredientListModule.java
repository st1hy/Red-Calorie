package com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.ingredientitems;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;

import javax.inject.Named;
import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

@Module
public class IngredientListModule {

    @NonNull
    private final View rootView;

    public IngredientListModule(@NonNull View rootView) {
        this.rootView = rootView;
    }

    @Named("ingredientListRow")
    @Provides
    public View rootView() {
        return rootView;
    }

    @Provides
    @PerIngredientRow
    @Named("ingredientImage")
    public static ImageView imageView(@Named("ingredientListRow") View rootView) {
        return (ImageView) rootView.findViewById(R.id.add_meal_ingredient_image);
    }

    @Provides
    @Named("ingredientImageHolder")
    public static ImageHolderDelegate imageHolderDelegate(@Named("appContext") Context context,
                                                          PermissionsHelper permissionsHelper,
                                                          @Named("ingredientImage") Provider<ImageView> imageView) {
        return new ImageHolderDelegate(context, permissionsHelper, imageView);
    }

}
