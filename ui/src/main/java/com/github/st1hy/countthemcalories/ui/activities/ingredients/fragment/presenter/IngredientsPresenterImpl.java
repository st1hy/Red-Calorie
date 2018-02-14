package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.presenter;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.QueryFinished;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.model.IngredientOptions;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.model.IngredientsFragmentModel;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.model.AddIngredientParams;
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate;
import com.github.st1hy.countthemcalories.ui.contract.IngredientsRepo;
import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoTransformer;
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogEvent;
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.ui.core.rx.Functions;
import com.github.st1hy.countthemcalories.ui.core.state.Visibility;
import com.github.st1hy.countthemcalories.ui.core.tokensearch.LastSearchResult;
import com.github.st1hy.countthemcalories.ui.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@PerFragment
public class IngredientsPresenterImpl implements IngredientsPresenter, IngredientViewHolder.Callback {

    @Inject
    LastSearchResult recentSearchResult;
    @Inject
    IngredientsDaoAdapter adapter;
    @Inject
    Observable<SearchResult> searchResultObservable;
    @Inject
    IngredientsView view;
    @Inject
    IngredientsRepo repo;
    @Inject
    DialogView dialogView;
    @Inject
    IngredientsFragmentModel model;

    private final Queue<Long> addedItems = new LinkedList<>();
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public IngredientsPresenterImpl() {
    }

    @Override
    public void onStart() {
        subscribe(
                view.getOnAddIngredientClickedObservable()
                        .map(any -> {
                            String extraName = recentSearchResult.get().getQuery();
                            return AddIngredientParams.of(AddIngredientType.MEAL, extraName);
                        })
                        .compose(view.addNewIngredient())
                        .map(IngredientTemplate::getId)
                        .subscribe(addedItems::offer)
        );
        onAdapterStart();
    }

    private void onAdapterStart() {
        subscribe(searchResultObservable
                .flatMap(search ->
                        repo.query(search.getQuery(), search.getTags())
                                .map(cursor -> QueryFinished.of(cursor, search))
                )
                .doOnError(e -> Timber.e(e, "Search exploded"))
                .retry(128)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(queryFinished -> {
                    Cursor cursor = queryFinished.getCursor();
                    SearchResult searchResult = queryFinished.getSearchingFor();
                    recentSearchResult.set(searchResult);
                    adapter.onCursorUpdate(cursor);
                    setNoIngredientVisibility(cursor);
                    adapter.notifyDataSetChanged();
                    onSearchFinished(cursor);
                }));
    }

    @Override
    public void onStop() {
        subscriptions.clear();
        adapter.closeCursor(true);
    }

    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }


    private void onSearchFinished(final Cursor cursor) {
        final Long newItemId = addedItems.poll();
        if (newItemId != null) {
            subscribe(
                    Observable.fromCallable(
                            () -> repo.findInCursor(cursor, newItemId))
                            .subscribeOn(Schedulers.computation())
                            .filter(IngredientsPresenterImpl::isSuccessful)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(pos -> view.scrollToPosition(IngredientsDaoAdapter.positionInAdapter(pos)))
            );
        }
    }

    private static boolean isSuccessful(int cursorPosition) {
        return cursorPosition != -1;
    }


    @Override
    public void onIngredientClicked(@NonNull final IngredientViewHolder viewHolder) {
        final IngredientTemplate ingredientTemplate = viewHolder.getReusableIngredient();
        if (model.isInSelectMode()) {
            view.onIngredientSelected(ingredientTemplate);
        } else {
            dialogView.showAlertDialog(model.getIngredientOptionsTitle(), model.getIngredientOptions())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(selectedOptionPosition -> {
                        IngredientOptions selectedOption = IngredientOptions.from(selectedOptionPosition);
                        switch (selectedOption) {
                            case ADD_TO_NEW:
                                view.addToNewMeal(ingredientTemplate);
                                break;
                            case EDIT:
                                onEditClicked(viewHolder);
                                break;
                            case REMOVE:
                                onDeleteClicked(viewHolder);
                                break;
                        }
                    });
        }
    }

    @Override
    public void onDeleteClicked(@NonNull IngredientViewHolder viewHolder) {
        viewHolder.setEnabled(false);
        final int positionInAdapter = viewHolder.getPositionInAdapter();
        long id = viewHolder.getReusableIngredient().getId();
        repo.query(id)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(template -> {
                    if (template.getChildIngredients().isEmpty())
                        return Observable.just(template);
                    else
                        return view.showUsedIngredientRemoveConfirmationDialog()
                                .doOnNext(event -> {
                                    if (event == DialogEvent.DISMISS) {
                                        viewHolder.setEnabled(true);
                                    }
                                })
                                .filter(event -> event == DialogEvent.POSITIVE)
                                .map(Functions.into(template));
                })
                .flatMap(repo::delete)
                .doOnNext(deleteResponse -> subscribe(deleteResponse.undoAvailability()
                        .compose(new UndoTransformer<>(deleteResponse,
                                isAvailable -> {
                                    if (isAvailable)
                                        return view.showUndoMessage(model.getUndoDeleteMessage());
                                    else {
                                        view.hideUndoMessage();
                                        return Observable.empty();
                                    }
                                }))
                        .doOnNext(adapter::onIngredientInserted)
                        .doOnNext(insertResult -> setNoIngredientVisibility(insertResult.getCursor()))
                        .subscribe()
                ))
                .map(Functions.intoResponse())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(cursor -> adapter.onIngredientDeleted(cursor, positionInAdapter))
                .doOnNext(this::setNoIngredientVisibility)
                .subscribe();
    }

    private void setNoIngredientVisibility(@NonNull Cursor cursor) {
        SearchResult searchingFor = recentSearchResult.get();
        boolean isSearchFilterEmpty = searchingFor.getQuery().trim().isEmpty() && searchingFor.getTags().isEmpty();
        view.setNoIngredientsMessage(isSearchFilterEmpty ? model.getNoIngredientsMessage() : model.getSearchEmptyMessage());
        view.setNoIngredientsVisibility(Visibility.of(cursor.getCount() == 0));
    }

    @Override
    public void onEditClicked(@NonNull IngredientViewHolder viewHolder) {
        long id = viewHolder.getReusableIngredient().getId();
        int positionInAdapter = viewHolder.getPositionInAdapter();
        //Ingredient template here is not attached to database and is missing tags
        repo.queryRecursive(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ingredientTemplate1 -> view.editIngredientTemplate(positionInAdapter, ingredientTemplate1), Timber::e);
    }

}