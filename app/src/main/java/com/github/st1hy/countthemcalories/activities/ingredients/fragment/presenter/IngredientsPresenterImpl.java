package com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.ingredients.model.AddIngredientParams;
import com.github.st1hy.countthemcalories.core.tokensearch.LastSearchResult;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class IngredientsPresenterImpl implements IngredientsPresenter {

    @NonNull
    private final IngredientsView view;
    @NonNull
    private final Provider<SearchResult> recentSearchResult;
    @NonNull
    private final IngredientsDaoAdapter adapter;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public IngredientsPresenterImpl(@NonNull IngredientsView view,
                                    @NonNull LastSearchResult recentSearchResult,
                                    @NonNull IngredientsDaoAdapter adapter) {
        this.view = view;
        this.recentSearchResult = recentSearchResult;
        this.adapter = adapter;
    }

    @Override
    public void onStart() {
        subscribe(
                view.getOnAddIngredientClickedObservable()
                        .compose(view.selectIngredientType())
                        .map(new Func1<AddIngredientType, AddIngredientParams>() {
                            @Override
                            public AddIngredientParams call(AddIngredientType type) {
                                String extraName = recentSearchResult.get().getQuery();
                                return new AddIngredientParams(type, extraName);
                            }
                        })
                        .compose(view.addNewIngredient())
                        .subscribe(new Action1<IngredientTemplate>() {
                            @Override
                            public void call(IngredientTemplate ingredientTemplate) {
                                adapter.onIngredientAdded(ingredientTemplate.getId());
                            }
                        })
        );

        adapter.onStart();

    }

    @Override
    public void onStop() {
        adapter.onStop();
        subscriptions.clear();
    }


    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }
}
