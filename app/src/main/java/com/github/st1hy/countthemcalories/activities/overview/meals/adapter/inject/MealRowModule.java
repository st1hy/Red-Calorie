package com.github.st1hy.countthemcalories.activities.overview.meals.adapter.inject;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.headerpicture.inject.HeaderImageView;

import dagger.Module;
import dagger.Provides;

@Module
public class MealRowModule {

    @LayoutRes
    private final int layoutRes;
    @NonNull
    private final ViewGroup parent;

    public MealRowModule(@LayoutRes int layoutRes,
                         @NonNull ViewGroup parent) {
        this.parent = parent;
        this.layoutRes = layoutRes;
    }

    @Provides
    @PerMealRow
    @MealItemRootView
    public View rootView() {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    @Provides
    @PerMealRow
    @HeaderImageView
    public static ImageView image(@MealItemRootView View rootView) {
        return (ImageView) rootView.findViewById(R.id.overview_item_image);
    }

}
