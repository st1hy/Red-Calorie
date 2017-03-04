package com.github.st1hy.countthemcalories.inject.activities.mealdetail.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.MealDetailFragment;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import org.parceler.Parcels;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.activities.mealdetail.fragment.MealDetailFragment.ARG_MEAL_PARCEL;

@Module(includes = MealDetailsBindings.class)
public class MealDetailsModule {

    private final MealDetailFragment fragment;
    private final Bundle savedState;

    public MealDetailsModule(@NonNull MealDetailFragment fragment, @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @Nullable
    public Bundle provideBundle() {
        return savedState;
    }


    @Provides
    @PerFragment
    public Meal provideMeal() {
        return Parcels.unwrap(fragment.getArguments().getParcelable(ARG_MEAL_PARCEL));
    }

    @Provides
    public Resources provideResources() {
        return fragment.getResources();
    }

    @Provides
    @PerFragment
    @Named("imageLoaderImageView")
    public static ImageView provideImageViewProvider(View rootView) {
        return (ImageView) rootView.findViewById(R.id.meal_detail_image);
    }

    @Provides
    public View rooView() {
        return fragment.getView();
    }

    @Provides
    @PerFragment
    public static RecyclerView recyclerView(@Named("activityContext") Context context,
                                            View rootView,
                                            RecyclerViewAdapterDelegate adapter) {

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.meal_detail_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return recyclerView;
    }

    @Provides
    @Named("mealImageHolder")
    public static ImageHolderDelegate imageHolderDelegate(ImageHolderDelegate imageHolderDelegate) {
        imageHolderDelegate.setImagePlaceholder(R.drawable.ic_fork_and_knife_positive);
        return imageHolderDelegate;
    }

}
