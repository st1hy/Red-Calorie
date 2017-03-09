package com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsViewController;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.core.DialogModule;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        PermissionModule.class,
        DialogModule.class
})
public abstract class IngredientsFragmentModule {

    @Binds
    public abstract IngredientsView provideView(IngredientsViewController controller);

    @Binds
    public abstract IngredientsPresenter ingredientsPresenter(IngredientsPresenterImpl presenter);

    @Provides
    public static RecyclerView ingredientsRecyclerView(@FragmentRootView View root,
                                                       @ActivityContext Context context,
                                                       IngredientsDaoAdapter adapter) {
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.ingredients_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Provides
    @Named("isInSelectMode")
    public static boolean selectMode(Intent intent) {
        return intent != null && IngredientsActivity.ACTION_SELECT_INGREDIENT.equals(intent.getAction());
    }

    @Provides
    @PerFragment
    public static RecyclerViewAdapterDelegate recyclerViewAdapterDelegate(RecyclerAdapterWrapper wrapper) {
        return RecyclerViewAdapterDelegate.newAdapter(wrapper);
    }
}
