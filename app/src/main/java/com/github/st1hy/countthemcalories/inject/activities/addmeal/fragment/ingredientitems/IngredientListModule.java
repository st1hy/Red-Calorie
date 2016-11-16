package com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.ingredientitems;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.ingredientitems.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.squareup.picasso.Picasso;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.internal.InstanceFactory;

@Module
public class IngredientListModule {

    @NonNull
    private final View rootView;
    @NonNull
    private final IngredientItemViewHolder.Callback callback;

    public IngredientListModule(@NonNull View rootView,
                                @NonNull IngredientItemViewHolder.Callback callback) {
        this.rootView = rootView;
        this.callback = callback;
    }

    @Named("ingredientImageHolder")
    @Provides
    public ImageHolderDelegate imageHolderDelegate(Picasso picasso,
                                                   PermissionsHelper permissionsHelper,
                                                   @Named("ingredientImage") ImageView imageView) {
        return new ImageHolderDelegate(picasso, permissionsHelper, InstanceFactory.create(imageView));
    }

    @Named("ingredientListRow")
    @Provides
    public View rootView() {
        return rootView;
    }

    @Provides
    @Named("ingredientImage")
    public ImageView imageView(@Named("ingredientListRow") View rootView) {
        return (ImageView) rootView.findViewById(R.id.add_meal_ingredient_image);
    }

    @Provides
    public IngredientItemViewHolder.Callback callback() {
        return callback;
    }
}
