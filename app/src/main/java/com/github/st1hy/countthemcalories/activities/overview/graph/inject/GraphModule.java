package com.github.st1hy.countthemcalories.activities.overview.graph.inject;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.graph.GraphFragment;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = GraphBinding.class)
public class GraphModule {

    private final GraphFragment fragment;

    public GraphModule(@NonNull GraphFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @Named("fragmentRoot")
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    @PerFragment
    public static RecyclerView recyclerView(@Named("fragmentRoot") View view,
                                            RecyclerViewAdapterDelegate adapter) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.graph_recycler);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Provides
    @PerFragment
    public static RecyclerViewAdapterDelegate recyclerViewAdapterDelegate(RecyclerAdapterWrapper wrapper) {
        return RecyclerViewAdapterDelegate.newAdapter(wrapper);
    }
}
