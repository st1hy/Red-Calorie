package com.github.st1hy.countthemcalories.inject.activities.overview.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.fragment.OverviewFragment;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = OverviewFragmentBindings.class)
public class OverviewFragmentModule {

    @NonNull
    private final OverviewFragment fragment;

    public OverviewFragmentModule(@NonNull OverviewFragment fragment) {
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
