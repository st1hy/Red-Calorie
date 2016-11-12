package com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class IngredientsPresenter implements BasicLifecycle {

    @NonNull
    private final IngredientsView view;
    @NonNull
    private final Provider<SearchResult> recentSearchResult;
    @NonNull
    private final IngredientsDaoAdapter adapter;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public IngredientsPresenter(@NonNull IngredientsView view,
                                @NonNull Provider<SearchResult> recentSearchResult,
                                @NonNull IngredientsDaoAdapter adapter) {
        this.view = view;
        this.recentSearchResult = recentSearchResult;
        this.adapter = adapter;
    }

    @Override
    public void onStart() {
        subscribe(
                view.getOnAddIngredientClickedObservable()
                        .flatMap(new Func1<Void, Observable<AddIngredientType>>() {
                            @Override
                            public Observable<AddIngredientType> call(Void aVoid) {
                                return view.selectIngredientType();
                            }
                        })
                        .flatMap(new Func1<AddIngredientType, Observable<IngredientTemplate>>() {
                            @Override
                            public Observable<IngredientTemplate> call(AddIngredientType type) {
                                String extraName = recentSearchResult.get().getQuery();
                                return view.addNewIngredient(type, extraName);
                            }
                        })
                        .subscribe(new Action1<IngredientTemplate>() {
                            @Override
                            public void call(IngredientTemplate ingredientTemplate) {
                                adapter.onIngredientAdded(ingredientTemplate.getId());
                            }
                        })
        );

    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }


    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }
}
