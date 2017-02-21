package com.github.st1hy.countthemcalories.inject.activities.overview.meals;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.inject.quantifier.datetime.MealPagerPosition;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment.ARG_CURRENT_PAGE;

@Module(includes = OverviewFragmentBindings.class)
public class OverviewFragmentModule {

    @NonNull
    private final MealsFragment fragment;
    @Nullable
    private final Bundle savedState;

    public OverviewFragmentModule(@NonNull MealsFragment fragment, @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @Named("fragmentRoot")
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    public static RecyclerView recyclerView(@Named("fragmentRoot") View view,
                                            RecyclerViewAdapterDelegate adapter,
                                            @Named("activityContext") Context context) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.overview_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return recyclerView;
    }

    @Provides
    @Nullable
    @Named("savedState")
    public Bundle savedState() {
        return savedState;
    }

    @Provides
    @Named("arguments")
    @Nullable
    public Bundle arguments() {
        return fragment.getArguments();
    }

    @Provides
    @MealPagerPosition
    public int currentDate(@Nullable @Named("arguments") Bundle arguments) {
        if (arguments != null) {
            return arguments.getInt(ARG_CURRENT_PAGE, -1);
        }
        return -1;
    }
}
