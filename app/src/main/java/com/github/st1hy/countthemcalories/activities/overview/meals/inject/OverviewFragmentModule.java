package com.github.st1hy.countthemcalories.activities.overview.meals.inject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.activities.overview.meals.adapter.MealInteraction;
import com.github.st1hy.countthemcalories.activities.overview.meals.adapter.MealsAdapter;
import com.github.st1hy.countthemcalories.activities.overview.meals.adapter.inject.MealRowComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.meals.presenter.MealsPresenter;
import com.github.st1hy.countthemcalories.activities.overview.meals.presenter.MealsPresenterImp;
import com.github.st1hy.countthemcalories.activities.overview.meals.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.meals.view.OverviewViewImpl;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;
import com.github.st1hy.countthemcalories.inject.quantifier.bundle.FragmentArguments;
import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

@Module(includes = {
        PermissionModule.class,
})
public abstract class OverviewFragmentModule {

    @Binds
    public abstract OverviewView provideView(OverviewViewImpl view);
    @Binds
    public abstract MealsPresenter provideDrawerPresenter(MealsPresenterImp presenter);
    @Binds
    public abstract RecyclerView.Adapter adapter(MealsAdapter presenter);
    @Binds
    public abstract MealRowComponentFactory mealRowComponentFactory(OverviewFragmentComponent component);

    @Provides
    public static RecyclerView recyclerView(@FragmentRootView View view,
                                            RecyclerView.Adapter adapter,
                                            @ActivityContext Context context) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.overview_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return recyclerView;
    }

    @Provides
    @MealPagerPosition
    public static int currentPage(@Nullable @FragmentArguments Bundle arguments) {
        if (arguments != null) {
            return arguments.getInt(MealsFragment.ARG_CURRENT_PAGE, -1);
        }
        return -1;
    }

    @Provides
    @PerFragment
    public static PublishSubject<MealInteraction> interactionSubject() {
        return PublishSubject.create();
    }
}
