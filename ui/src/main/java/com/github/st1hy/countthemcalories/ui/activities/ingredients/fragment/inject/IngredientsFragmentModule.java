package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.inject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.presenter.IngredientsPresenterImpl;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.view.IngredientsViewController;
import com.github.st1hy.countthemcalories.ui.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.subjects.PublishSubject;

@Module(includes = {
        DialogModule.class
})
public abstract class IngredientsFragmentModule {

    @Binds
    public abstract IngredientsView provideView(IngredientsViewController controller);

    @Binds
    public abstract IngredientsPresenter ingredientsPresenter(IngredientsPresenterImpl presenter);

    @Binds
    public abstract IngredientViewHolder.Callback viewHolderCallback(IngredientsPresenterImpl presenter);

    @Provides
    public static RecyclerView ingredientsRecyclerView(@FragmentRootView View root,
                                                       @ActivityContext Context context,
                                                       IngredientsDaoAdapter adapter) {
        RecyclerView recyclerView = root.findViewById(R.id.ingredients_content);
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
    public static PublishSubject<RecyclerEvent> eventSubject() {
        return PublishSubject.create();
    }

    @Provides
    @PerFragment
    public static Observable<RecyclerEvent> recyclerEventObservable(PublishSubject<RecyclerEvent> events) {
        return events.asObservable();
    }

}
