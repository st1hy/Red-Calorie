package com.github.st1hy.countthemcalories.activities.overview.graph.inject;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.graph.presenter.GraphDataAdapter;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

@Module
public abstract class GraphModule {

    @Binds
    public abstract GraphColumnComponentFactory columnFactory(GraphComponent component);

    @Provides
    @PerFragment
    public static RecyclerView recyclerView(@FragmentRootView View view,
                                            GraphDataAdapter adapter) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.graph_recycler);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Provides
    @PerFragment
    @Named("graphItemClicked")
    public static PublishSubject<Integer> graphItemClicked() {
        return PublishSubject.create();
    }
}
