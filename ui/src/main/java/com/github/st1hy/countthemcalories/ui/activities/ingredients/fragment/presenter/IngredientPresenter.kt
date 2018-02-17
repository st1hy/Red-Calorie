package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.presenter

import android.database.Cursor
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model.AddIngredientType
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.IngredientsDaoAdapter
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.QueryFinished
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.viewholder.IngredientViewHolder
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.inject.IngredientClicks
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.model.IngredientClick
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.model.IngredientOptions
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.model.IngredientsFragmentModel
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.view.IngredientsView
import com.github.st1hy.countthemcalories.ui.activities.ingredients.model.AddIngredientParams
import com.github.st1hy.countthemcalories.ui.contract.CommandResponse
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate
import com.github.st1hy.countthemcalories.ui.contract.IngredientsRepo
import com.github.st1hy.countthemcalories.ui.contract.InsertResult
import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoAction
import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoTransformer
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogEvent
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogView
import com.github.st1hy.countthemcalories.ui.core.rx.Functions
import com.github.st1hy.countthemcalories.ui.core.state.Visibility
import com.github.st1hy.countthemcalories.ui.core.tokensearch.LastSearchResult
import com.github.st1hy.countthemcalories.ui.core.tokensearch.SearchResult
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@PerFragment internal class IngredientsPresenterImpl @Inject constructor() : IngredientsPresenter {

    @Inject lateinit var recentSearchResult: LastSearchResult
    @Inject lateinit var adapter: IngredientsDaoAdapter
    @Inject lateinit var searchResultObservable: Observable<SearchResult>
    @Inject lateinit var view: IngredientsView
    @Inject lateinit var repo: IngredientsRepo
    @Inject lateinit var dialogView: DialogView
    @Inject lateinit var model: IngredientsFragmentModel
    @Inject @IngredientClicks lateinit var clickObservable: Observable<IngredientClick>

    private val addedItems = LinkedList<Long>()
    private val subscriptions = CompositeSubscription()

    override fun onStart() {
        subscriptions.add(view.getOnAddIngredientClickedObservable()
                .map {
                    val extraName = recentSearchResult.get().query
                    AddIngredientParams.of(AddIngredientType.MEAL, extraName)
                }
                .compose(view.addNewIngredient())
                .map { template -> template.id!! }
                .subscribe { id -> addedItems.offer(id) }
        )
        onAdapterStart()
    }

    private fun onAdapterStart() {
        subscriptions.add(searchResultObservable
                .flatMap { search ->
                    repo.query(search.query, search.tags)
                            .map { cursor -> QueryFinished.of(cursor, search) }
                }
                .doOnError { e -> Timber.e(e, "Search exploded") }
                .retry(128)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { queryFinished ->
                    val cursor = queryFinished.cursor
                    val searchResult = queryFinished.searchingFor
                    recentSearchResult.set(searchResult)
                    adapter.onCursorUpdate(cursor)
                    setNoIngredientVisibility(cursor)
                    adapter.notifyDataSetChanged()
                    onSearchFinished(cursor)
                })
        subscriptions.add(clickObservable.subscribe { event ->
            val holder = event.holder
            when (event.type) {
                IngredientClick.Type.OPEN -> onIngredientClicked(holder)
                IngredientClick.Type.EDIT -> onEditClicked(holder)
                IngredientClick.Type.DELETE -> onDeleteClicked(holder)
            }
        })
    }

    override fun onStop() {
        subscriptions.clear()
        adapter.closeCursor(true)
    }


    private fun onSearchFinished(cursor: Cursor) {
        val newItemId = addedItems.poll()
        if (newItemId != null) {
            subscriptions.add(Observable.fromCallable { repo.findInCursor(cursor, newItemId) }
                    .subscribeOn(Schedulers.computation())
                    .filter({ isSuccessful(it) })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { position ->
                        view.scrollToPosition(IngredientsDaoAdapter.positionInAdapter(position))
                    }
            )
        }
    }

    private fun isSuccessful(cursorPosition: Int): Boolean {
        return cursorPosition != -1
    }


    private fun onIngredientClicked(viewHolder: IngredientViewHolder) {
        val ingredientTemplate = viewHolder.reusableIngredient
        if (model.isInSelectMode) {
            view.onIngredientSelected(ingredientTemplate)
        } else {
            dialogView.showAlertDialog(model.ingredientOptionsTitle,
                    model.ingredientOptions)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { selectedOptionPosition ->
                        val selectedOption = IngredientOptions.from(selectedOptionPosition)
                        when (selectedOption) {
                            IngredientOptions.ADD_TO_NEW -> view.addToNewMeal(ingredientTemplate)
                            IngredientOptions.EDIT -> onEditClicked(viewHolder)
                            IngredientOptions.REMOVE -> onDeleteClicked(viewHolder)
                            else -> {
                            }
                        }
                    }
        }
    }

    private fun onDeleteClicked(viewHolder: IngredientViewHolder) {
        viewHolder.setEnabled(false)
        val positionInAdapter = viewHolder.getPositionInAdapter()
        val id = viewHolder.reusableIngredient.id ?: return
        repo.query(id).observeOn(AndroidSchedulers.mainThread())
                .flatMap { template -> checkIfUsed(template, viewHolder) }
                .flatMap(repo::delete)
                .doOnNext(this::maybeUndoDelete)
                .map(Functions.intoResponse<Cursor>())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ cursor -> adapter.onIngredientDeleted(cursor, positionInAdapter) })
                .doOnNext(this::setNoIngredientVisibility)
                .subscribe()
    }

    private fun maybeUndoDelete(deleteResponse: CommandResponse<Cursor, InsertResult>) {
        subscriptions.add(deleteResponse.undoAvailability()
                .compose(UndoTransformer<Cursor, InsertResult>(
                        deleteResponse) { isAvailable ->
                    if (isAvailable!!)
                        return@UndoTransformer view.showUndoMessage(
                                model.undoDeleteMessage)
                    else {
                        view.hideUndoMessage()
                        return@UndoTransformer Observable.empty<UndoAction>()
                    }
                })
                .doOnNext(adapter::onIngredientInserted)
                .doOnNext({ insertResult ->
                    setNoIngredientVisibility(insertResult.cursor)
                })
                .subscribe()
        )
    }

    private fun checkIfUsed(template: IngredientTemplate,
                            viewHolder: IngredientViewHolder): Observable<IngredientTemplate> {
        return if (template.childIngredients.isEmpty())
            Observable.just(template)
        else
            view.showUsedIngredientRemoveConfirmationDialog()
                    .doOnNext({ event ->
                        if (event == DialogEvent.DISMISS) {
                            viewHolder.setEnabled(true)
                        }
                    })
                    .filter({ event -> event == DialogEvent.POSITIVE })
                    .map(Functions.into(template))
    }

    private fun setNoIngredientVisibility(cursor: Cursor) {
        val searchingFor = recentSearchResult.get()
        val isSearchFilterEmpty = searchingFor.query.isEmpty() && searchingFor.tags.isEmpty()
        view.setNoIngredientsMessage(
                if (isSearchFilterEmpty) model.noIngredientsMessage else model.searchEmptyMessage)
        view.setNoIngredientsVisibility(Visibility.of(cursor.count == 0))
    }

    private fun onEditClicked(viewHolder: IngredientViewHolder) {
        val id = viewHolder.reusableIngredient.id ?: return
        val positionInAdapter = viewHolder.getPositionInAdapter()
        //Ingredient template here isSelected not attached to database and isSelected missing tags
        repo.queryRecursive(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ template ->
                    view.editIngredientTemplate(positionInAdapter.toLong(), template)
                }, { error -> Timber.e(error) })
    }

}