package com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.inject;

import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject.IngredientRootView;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject.PerIngredientRow;
import com.github.st1hy.countthemcalories.core.headerpicture.inject.HeaderImageView;

import dagger.Module;
import dagger.Provides;

@Module
public class IngredientViewHolderModule {

    private final View rootView;

    public IngredientViewHolderModule(View rootView) {
        this.rootView = rootView;
    }

    @Provides
    @PerIngredientRow
    @HeaderImageView
    public ImageView imageView() {
        return (ImageView) rootView.findViewById(R.id.ingredients_item_image);
    }

    @Provides
    @IngredientRootView
    public View rootView() {
        return rootView;
    }


}
