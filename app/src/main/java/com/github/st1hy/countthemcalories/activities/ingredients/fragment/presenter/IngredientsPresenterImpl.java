package com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.QueryFinished;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientOptions;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientsFragmentModel;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.ingredients.model.AddIngredientParams;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.commands.IngredientsDatabaseCommands;
import com.github.st1hy.countthemcalories.core.command.undo.UndoTransformer;
import com.github.st1hy.countthemcalories.core.dialog.DialogEvent;
import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.tokensearch.LastSearchResult;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientOptions.from;

@PerFragment
public class IngredientsPresenterImpl implements IngredientsPresenter, IngredientViewHolder.Callback{

    @Inject
    LastSearchResult recentSearchResult;
    @Inject
    IngredientsDaoAdapter adapter;
    @Inject
    Observable<SearchResult> searchResultObservable;
    @Inject
    RxIngredientsDatabaseModel databaseModel;
    @Inject
    IngredientsView view;
    @Inject
    IngredientsDatabaseCommands commands;
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
                        databaseModel.getAllFilteredBy(search.getQuery(), search.getTokens())
                                .map(cursor -> QueryFinished.of(cursor, search))
                )
                .doOnError(e -> Timber.e(e, "Search exploded"))
                .retry(128)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(queryFinished -> {
                    SearchResult searchResult = queryFinished.getSearchingFor();
                    recentSearchResult.set(searchResult);
                    adapter.onCursorUpdate(queryFinished.getCursor());
                    setNoIngredientVisibility(queryFinished.getCursor());
                    adapter.notifyDataSetChanged();
                    onSearchFinished();
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


    private void onSearchFinished() {
        final Long newItemId = addedItems.poll();
        if (newItemId != null) {
            subscribe(
                    Observable.fromCallable(
                            () -> adapter.findPositionById(newItemId))
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
                        IngredientOptions selectedOption = from(selectedOptionPosition);
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
        databaseModel.getById(id)
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
                .flatMap(commands::delete)
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
        boolean isSearchFilterEmpty = searchingFor.getQuery().trim().isEmpty() && searchingFor.getTokens().isEmpty();
        view.setNoIngredientsMessage(isSearchFilterEmpty ? model.getNoIngredientsMessage() : model.getSearchEmptyMessage());
        view.setNoIngredientsVisibility(Visibility.of(cursor.getCount() == 0));
    }

    @Override
    public void onEditClicked(@NonNull IngredientViewHolder viewHolder) {
        long id = viewHolder.getReusableIngredient().getId();
        int positionInAdapter = viewHolder.getPositionInAdapter();
        //Ingredient template here is not attached to database and is missing tags
        databaseModel.getByIdRecursive(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ingredientTemplate1 -> {
                    view.editIngredientTemplate(positionInAdapter, ingredientTemplate1);
                });
    }

}
