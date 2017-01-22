package com.github.st1hy.countthemcalories.inject.activities.ingredients.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.IngredientsFragment;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = IngredientFragmentBindings.class)
public class IngredientsFragmentModule {

    private final IngredientsFragment fragment;

    public IngredientsFragmentModule(IngredientsFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    public Resources provideResources() {
        return fragment.getResources();
    }

    @Provides
    public static RecyclerView ingredientsRecyclerView(View root,
                                                       @Named("activityContext") Context context,
                                                       IngredientsDaoAdapter adapter) {
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.ingredients_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Provides
    @Named("isInSelectMode")
    public boolean selectMode() {
        return fragment.getArguments().getBoolean(IngredientsFragment.ARG_SELECT_BOOL, false);
    }

    @Provides
    @PerFragment
    public static RecyclerViewAdapterDelegate recyclerViewAdapterDelegate(RecyclerAdapterWrapper wrapper) {
        return RecyclerViewAdapterDelegate.newAdapter(wrapper);
    }
}
