package com.github.st1hy.countthemcalories.ui.activities.tags.presenter

import android.database.Cursor
import android.support.annotation.CheckResult
import android.support.annotation.StringRes
import com.github.st1hy.countthemcalories.ui.activities.tags.adapter.TagsDaoAdapter
import com.github.st1hy.countthemcalories.ui.activities.tags.adapter.holder.TagItemHolder
import com.github.st1hy.countthemcalories.ui.activities.tags.adapter.holder.TagViewHolder
import com.github.st1hy.countthemcalories.ui.activities.tags.model.ClickEvent
import com.github.st1hy.countthemcalories.ui.activities.tags.model.TagsFragmentModel
import com.github.st1hy.countthemcalories.ui.activities.tags.model.TagsViewModel
import com.github.st1hy.countthemcalories.ui.activities.tags.model.Type
import com.github.st1hy.countthemcalories.ui.activities.tags.view.TagsView
import com.github.st1hy.countthemcalories.ui.contract.*
import com.github.st1hy.countthemcalories.ui.core.baseview.Click
import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoAction
import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoTransformer
import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoView
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogEvent
import com.github.st1hy.countthemcalories.ui.core.rx.Functions
import com.github.st1hy.countthemcalories.ui.core.rx.SimpleSubscriber
import com.github.st1hy.countthemcalories.ui.core.state.Visibility
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment
import com.google.common.base.Strings
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.functions.Func1
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@PerFragment internal class TagsPresenter @Inject constructor() {

    private val debounceTime: Long = 250

    @Inject lateinit var adapter: TagsDaoAdapter
    @Inject lateinit var tagsRepo: TagsRepo
    @Inject lateinit var view: TagsView
    @Inject lateinit var fragmentModel: TagsFragmentModel
    @Inject lateinit var viewModel: TagsViewModel
    @Inject lateinit var stateChanges: PublishSubject<TagViewHolder>
    @Inject lateinit var undoView: UndoView
    @Inject lateinit var clickEvents: PublishSubject<ClickEvent>
    @Inject lateinit var tagFactory: TagFactory

    private var lastQuery = ""

    private val subscriptions = CompositeSubscription()

    fun onStart() {
        onAddTagClicked(view.addTagClickedObservable())
        subscriptions.addAll(
                view.getQueryObservable()
                        .compose(searchDatabase())
                        .subscribe(),
                stateChanges
                        .filter { holder -> holder is TagItemHolder }
                        .cast(TagItemHolder::class.java)
                        .subscribe { onViewHolderStateChanged(it) },
                view.confirmClickedObservable().subscribe { view.onTagsSelected(fragmentModel.tags) },
                clickEvents.subscribe { event ->
                    when (event.type) {
                        Type.EDIT -> onEditClicked(event.position, event.holder)
                        Type.OPEN -> onTagClicked(event.holder)
                        Type.REMOVE -> onDeleteClicked(event.position, event.holder)
                    }
                }
        )
        view.setConfirmButtonVisibility(Visibility.of(fragmentModel.isInSelectMode))
        adapter.onStart()
    }

    fun onStop() {
        subscriptions.clear()
        adapter.onStop()
    }

    @CheckResult
    private fun searchDatabase(): Observable.Transformer<CharSequence, QueryResult> {
        return Observable.Transformer { charSequenceObservable ->
            var sequenceObservable = charSequenceObservable!!
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .share()
            sequenceObservable = sequenceObservable
                    .limit(1)
                    .concatWith(
                            sequenceObservable
                                    .skip(1)
                                    .debounce(debounceTime, TimeUnit.MILLISECONDS)
                    )
            sequenceObservable
                    .map(CharSequence::toString)
                    .flatMap({ query ->
                        tagsRepo.query(query)
                                .map({ cursor -> QueryResult.of(query, cursor) })
                    })
                    .retry(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext({ result ->
                        lastQuery = result.query
                        onCursorUpdate(result)
                        adapter.notifyDataSetChanged()
                    })
        }
    }

    private fun onCursorUpdate(lastQuery: String, cursor: Cursor) {
        onCursorUpdate(QueryResult.of(lastQuery, cursor))
    }

    private fun onCursorUpdate(result: QueryResult) {
        adapter.onCursorUpdate(result.cursor)
        val query = result.query
        val cursor = result.cursor
        view.setNoTagsMessage(if (query.isBlank())
            viewModel.noTagsMessage
        else
            viewModel.searchResultEmptyMessage)
        view.setNoTagsVisibility(Visibility.of(cursor.count == 0))
    }


    private fun onAddTagClicked(clicks: Observable<Click>) {
        subscriptions.add(
                clicks.subscribeOn(AndroidSchedulers.mainThread())
                        .flatMap { return@flatMap view.newTagDialog(viewModel.newTagDialogTitle, lastQuery) }
                        .map(String::trim)
                        .filter({ s -> !Strings.isNullOrEmpty(s) })
                        .map({ tagName -> tagFactory.newTag(tagName) })
                        .flatMap { tag -> return@flatMap tagsRepo.insert(tag) }
                        .doOnNext({ addResponse ->
                            subscriptions.add(addResponse.undoAvailability()
                                    .compose(UndoTransformer(addResponse,
                                            showUndoMessage(viewModel.undoAddMessage)))
                                    .subscribe(onTagRemoved(addResponse.response.newItemPositionInCursor))
                            )
                        })
                        .map(Functions.intoResponse())
                        .retry()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onTagAdded())
        )
    }

    private fun onViewHolderStateChanged(tagItemHolder: TagItemHolder) {
        val reusableTag = tagItemHolder.reusableTag
        val isChecked = tagItemHolder.isChecked
        fragmentModel.setSelected(reusableTag, isChecked)
    }


    private fun onTagClicked(holder: TagItemHolder) {
        val tag = holder.reusableTag
        if (fragmentModel.isInSelectMode) {
            val isChecked = !holder.isChecked
            holder.isChecked = isChecked
            fragmentModel.setSelected(tag, isChecked)
        } else {
            view.openIngredientsFilteredBy(tag.displayName)
        }
    }

    private fun onEditClicked(positionInAdapter: Int, holder: TagItemHolder) {
        val tag = holder.reusableTag
        subscriptions.add(
                view.newTagDialog(viewModel.editTagDialogTitle, tag.displayName)
                        .flatMap { newName ->
                            tag.name = newName
                            tag.creationSource = CreationSource.USER
                            tagsRepo.update(tag)
                        }
                        .map { cursor -> InnerResult(cursor, tagsRepo.findInCursor(cursor, tag)) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onEditFinished(positionInAdapter))
        )
    }

    class InnerResult(override val cursor: Cursor,
                      override val newItemPositionInCursor: Int) : InsertResult

    private fun onEditFinished(position: Int): SimpleSubscriber<InsertResult> {
        return object : SimpleSubscriber<InsertResult>() {
            override fun onNext(result: InsertResult) {
                onCursorUpdate(lastQuery, result.cursor)
                val newPosition = result.newItemPositionInCursor
                adapter.onNewItemPositionInCursor(position, newPosition)
            }
        }
    }

    private fun onDeleteClicked(position: Int, holder: TagItemHolder) {
        val tagId = holder.reusableTag.id!!
        holder.setEnabled(false)
        subscriptions.add(
                tagsRepo.query(tagId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap { tag -> return@flatMap checkIfUsed(tag, holder) }
                        .flatMap(tagsRepo::delete)
                        .doOnNext({ deleteResponse ->
                            subscriptions.add(deleteResponse.undoAvailability()
                                    .compose(UndoTransformer(deleteResponse,
                                            showUndoMessage(viewModel.undoDeleteMessage))
                                    )
                                    .subscribe(onTagAdded())
                            )
                        })
                        .map(Functions.intoResponse())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onTagRemoved(position))
        )
    }

    private fun checkIfUsed(tag: Tag, holder: TagItemHolder) : Observable<Tag> {
        val tagId = tag.id!!
        if (tag.ingredientTypes.isEmpty())
            return tagsRepo.query(tagId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap { Observable.just(tag) }
        else
            return tagsRepo.query(tagId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap { return@flatMap view.showRemoveTagDialog() }
                    .doOnNext({ event ->
                        if (event === DialogEvent.DISMISS) {
                            holder.setEnabled(true)
                        }
                    })
                    .filter({ event -> event === DialogEvent.POSITIVE })
                    .map(Functions.into(tag))
    }

    private fun onTagRemoved(positionInAdapter: Int): Action1<Cursor> {
        return Action1 { cursor ->
            onCursorUpdate(lastQuery, cursor)
            adapter.onPositionRemoved(positionInAdapter)
        }
    }

    private fun onTagAdded(): Action1<InsertResult> {
        return Action1 { result ->
            val newItemPosition = result.newItemPositionInCursor
            onCursorUpdate(lastQuery, result.cursor)
            if (adapter.onPositionAdded(newItemPosition)) {
                view.scrollToPosition(newItemPosition)
            }
        }
    }

    private fun showUndoMessage(@StringRes undoMessage: Int)
            : Func1<Boolean, Observable<UndoAction>> {
        return Func1 { isAvailable ->
            return@Func1 if (isAvailable!!)
                undoView.showUndoMessage(undoMessage)
            else {
                undoView.hideUndoMessage()
                Observable.empty<UndoAction>()
            }
        }
    }

    class QueryResult private constructor(val query: String, val cursor: Cursor) {
        companion object {

            fun of(query: String, cursor: Cursor): QueryResult {
                return QueryResult(query, cursor)
            }
        }
    }
}