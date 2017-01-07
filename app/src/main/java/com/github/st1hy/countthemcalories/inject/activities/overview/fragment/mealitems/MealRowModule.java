package com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.squareup.picasso.Picasso;

import javax.inject.Named;
import javax.inject.Provider;

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
    @Named("mealItemRoot")
    public View rootView() {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    @Provides
    public static ImageHolderDelegate imageHolderDelegate(Picasso picasso,
                                                          PermissionsHelper permissionHelper,
                                                          Provider<ImageView> imageView) {
        return new ImageHolderDelegate(picasso, permissionHelper, imageView);
    }

    @Provides
    @PerMealRow
    public static ImageView image(@Named("mealItemRoot") View rootView) {
        return (ImageView) rootView.findViewById(R.id.overview_item_image);
    }

}
