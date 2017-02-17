package com.github.st1hy.countthemcalories.inject.activities.overview.meals;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = OverviewFragmentBindings.class)
public class OverviewFragmentModule {

    @NonNull
    private final MealsFragment fragment;

    public OverviewFragmentModule(@NonNull MealsFragment fragment) {
        this.fragment = fragment;
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


}
