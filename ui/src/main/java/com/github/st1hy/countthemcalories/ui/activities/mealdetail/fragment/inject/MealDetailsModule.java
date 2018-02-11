package com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.inject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.MealDetailActivity;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.adapter.MealIngredientsAdapter;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.presenter.MealDetailPresenter;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.presenter.MealDetailPresenterImpl;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.view.MealDetailViewImpl;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder.DetailImageHolderDelegate;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.HeaderImageView;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView;

import org.parceler.Parcels;

import java.util.List;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class MealDetailsModule {

    @Binds
    public abstract MealDetailView provideView(MealDetailViewImpl mealDetailView);

    @Binds
    public abstract MealDetailPresenter providePresenter(MealDetailPresenterImpl presenter);

    @Provides
    @PerFragment
    public static Meal provideMeal(Intent intent) {
        return Parcels.unwrap(intent.getParcelableExtra(MealDetailActivity.EXTRA_MEAL_PARCEL));
    }

    @Provides
    public static List<Ingredient> ingredients(Meal meal) {
        return meal.getIngredients();
    }

    @Provides
    @PerFragment
    @HeaderImageView
    public static ImageView provideImageViewProvider(@FragmentRootView View rootView) {
        return (ImageView) rootView.findViewById(R.id.meal_detail_image);
    }

    @Provides
    @PerFragment
    public static RecyclerView recyclerView(@ActivityContext Context context,
                                            @FragmentRootView View rootView,
                                            MealIngredientsAdapter adapter) {

        RecyclerView recyclerView = rootView.findViewById(R.id.meal_detail_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return recyclerView;
    }

    @Provides
    @PerFragment
    public static ImageHolderDelegate imageHolderDelegate(DetailImageHolderDelegate imageHolderDelegate) {
        imageHolderDelegate.setImagePlaceholder(R.drawable.ic_fork_and_knife_positive);
        return imageHolderDelegate;
    }

}
